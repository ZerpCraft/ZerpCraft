package com.ezpzxrp.zerpcraft.callouts;

import okhttp3.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;

public class S1 {

    private static OkHttpClient client;

    public S1() {

       client = new OkHttpClient().newBuilder().build();
    }
    public JSONObject getWalletAccountInfo(String publicAddress) {

        String signRequestJSONBody = "{\n" +
                "    \"method\": \"account_info\",\n" +
                "    \"params\": [\n" +
                "        {\n" +
                "            \"account\": \"" + publicAddress + "\",\n" +
                "            \"strict\": true,\n" +
                "            \"ledger_index\": \"validated\",\n" +
                "            \"api_version\": 1\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"), signRequestJSONBody
        );

        Request request = new Request.Builder()
                .url("http://s1.ripple.com:51234/")
                .method("POST", body)
                .build();

        try {

            Response response = client.newCall(request).execute();
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(response.body().string());
        }
        catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
 }
