package com.ezpzxrp.zerpcraft.XUMM;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.config.Config;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fl.xrpl4j.model.jackson.ObjectMapperFactory;
import com.fl.xrpl4j.model.transactions.*;
import com.fl.xrpl4j.model.transactions.Address;
import com.fl.xumm4j.sdk.builder.CredentialsBuilder;
import com.fl.xumm4j.sdk.XummClient;
import com.fl.xumm4j.sdk.Deserialize;
import com.fl.xumm4j.sdk.builder.PayloadBuilder;
import okhttp3.*;
import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

public class XUMM extends Exception {

    private String apiKey;
    private String apiSecret;
    private CredentialsBuilder credentialsBuilder;
    private XummClient xummClient;
    private Deserialize deserialize;

    public XUMM() throws XUMM {

        setApiKey();
        setApiSecret();
        this.credentialsBuilder = new CredentialsBuilder.builder().apiKey(this.apiKey).secretKey(this.apiSecret).build();
        this.xummClient = new XummClient(this.credentialsBuilder);
        this.deserialize = new Deserialize();
    }

    private void setApiKey() throws XUMM {

        this.apiKey = Config.getInstance().getXUMMApiKey();
        if(this.apiKey == null) {
            throw new XUMM("XUMM API Key is empty. Follow config.yml instructions to setup a key");
        }
    }

    private void setApiSecret() throws XUMM {

        this.apiSecret = Config.getInstance().getXUMMApiSecret();
        if(this.apiSecret == null) {
            throw new XUMM("XUMM API Secret is empty. Follow config.yml instructions to setup a key");
        }
    }

    public String signRequest() {

        ZerpCraft.p.getLogger().info("Building payload");
        String payload = new PayloadBuilder.builder()
                .txjson("{\"TransactionType\": \"SignIn\"}")
                .expire(3)
                .instruction("This is a login request.")
                .build();

        ZerpCraft.p.getLogger().info("POSTing payload");
        String payloadResponse = xummClient.postPayload(payload);

        return payloadResponse;
    }

    public String getUserTokenRequest(String registrationUUID) {

        String payloadResponse = xummClient.getPayload(registrationUUID);

        return payloadResponse;
    }

    public String paymentRequest(String paymentDestination, double paymentAmount, String userToken, String account) {

        String JSON = null;
        
        Payment payment = Payment.builder()
                .fee(XrpCurrencyAmount.ofDrops(12))
                .destination(Address.of(paymentDestination))
                .account(Address.of(account))
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(paymentAmount)))
                .build();

        try {

            ObjectMapper objectMapper = ObjectMapperFactory.create();
            JSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payment);
            String payload = new PayloadBuilder.builder()
                    .txjson(JSON).userToken(userToken) //Pass the generated JSON Object.
                    .build();
            System.out.println(payload);
            JSON = xummClient.postPayload(payload);
        }
        catch (JsonProcessingException ignored) {}
        
        return JSON;
    }
    public String paymentRequest(String paymentDestination, double paymentAmount, String userToken, int expiry, String account) {

        String JSON = null;

        Payment payment = Payment.builder()
                .fee(XrpCurrencyAmount.ofDrops(12))
                .destination(Address.of(paymentDestination))
                .account(Address.of(account))
                .amount(XrpCurrencyAmount.ofXrp(BigDecimal.valueOf(paymentAmount)))
                .build();

        try {

            ObjectMapper objectMapper = ObjectMapperFactory.create();
            JSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(payment);
            String payload = new PayloadBuilder.builder()
                    .txjson(JSON).userToken(userToken) //Pass the generated JSON Object.
                    .expire(expiry)
                    .build();
            System.out.println(payload);
            JSON = xummClient.postPayload(payload);
        }
        catch (JsonProcessingException ignored) {}

        return JSON;
    }

    public String stampNFT(String hash, String userToken, String account) {

        String JSON = null;
        String hexHash = String.valueOf(Hex.encodeHex(hash.getBytes(StandardCharsets.UTF_8)));


        ImmutableAccountSet accountSet = AccountSet.builder()
                .fee(XrpCurrencyAmount.ofDrops(12))
                .domain(hexHash)
                .account(Address.of(account))
                .build();

        try {

            ObjectMapper objectMapper = ObjectMapperFactory.create();
            JSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountSet);
            String payload = new PayloadBuilder.builder()
                    .txjson(JSON).userToken(userToken) //Pass the generated JSON Object.
                    .build();
            System.out.println(payload);
            JSON = xummClient.postPayload(payload);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(JSON);
        return JSON;
    }

    public String stampNFT(String userToken) {

        String JSON = null;


        ImmutableAccountSet accountSet = AccountSet.builder()
                .fee(XrpCurrencyAmount.ofDrops(12))
                .domain("")
                .build();

        try {

            ObjectMapper objectMapper = ObjectMapperFactory.create();
            JSON = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(accountSet);
            String payload = new PayloadBuilder.builder()
                    .txjson(JSON).userToken(userToken) //Pass the generated JSON Object.
                    .build();
            System.out.println(payload);
            JSON = xummClient.postPayload(payload);
        }
        catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println(JSON);
        return JSON;
    }

    public Boolean validatePaymentAmount(String currencyType, Double paymentAmount) {

        if(currencyType.equals("XRP") && paymentAmount < .000001) {

            // TODO: This messaging should be a top level message to the player
            //player.sendMessage("The given amount is too small! Try an amount larger than .000001");
            return false;
        }

        return true;
    }

    public void nfTokenAcceptRequest(String sellOffer, String userToken, String account) throws XUMM {

        String paymentRequestJsonBody = "{ \n" +
                "  \"txjson\": {\n" +
                "    \"TransactionType\": \"NFTokenAcceptOffer\",\n" +
                "    \"Account\": \"" + account + "\",\n" +
                "    \"SellOffer\": \"" + sellOffer + "\"\n" +
                "  },\n" +
                "  \"user_token\": \"" + userToken + "\"\n" +
                "}";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), paymentRequestJsonBody
        );

        System.out.println(paymentRequestJsonBody);
        // I can probably pull the code below out into a separate method to avoid duplication
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        Request request = new Request.Builder()
                .url("https://xumm.app/api/v1/platform/payload")
                .method("POST", body)
                .addHeader("x-api-key", this.apiKey)
                .addHeader("x-api-secret",this.apiSecret)
                .addHeader("content-type","application/json")
                .build();

        try {

            ZerpCraft.p.getLogger().info("Pushing the NFTokenAcceptOffer");
            client.newCall(request).execute();
        } catch (IOException e) {

            ZerpCraft.p.getLogger().info("NFTokenAcceptOffer Exception?");
            e.printStackTrace();
            throw new XUMM("XUMM API Key is empty. Follow config.yml instructions to setup a key");
        }
    }

    public XUMM(String errorMessage) {
        super(errorMessage);
    }
}