package com.ezpzxrp.zerpcraft.XRPL;

import com.ezpzxrp.zerpcraft.LandAdmin.runnables.NFTBurnTask;
import com.ezpzxrp.zerpcraft.LandAdmin.runnables.NFTTransferEventTask;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import jakarta.websocket.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xrpl.xrpl4j.codec.addresses.AddressCodec;
import org.xrpl.xrpl4j.codec.addresses.UnsignedByteArray;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class XRPLWebsocketClient {

    Session session = null;
    private MessageHandler handler;

    public XRPLWebsocketClient(String connectionString) {

        try {

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(connectionString));
        }
         catch (DeploymentException | IOException | URISyntaxException e) {

            throw new RuntimeException(e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {

        this.session = session;
        System.out.println("Opening websocket");
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        System.out.println("Incoming message!");
        handleWebsocketMessage(message, session);
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {

        System.out.println("Websocket Closing");
    }

    public static interface MessageHandler {

        public void handleMessage(String message);
    }

    public void handleWebsocketMessage(String message, Session session) {

        try {

            this.handler.handleMessage(message);
            JSONParser parser = new JSONParser();
            JSONObject eventObject = (JSONObject) parser.parse(message);
            System.out.println(eventObject);
            if (eventObject.get("transaction") == null) {

                return;
            }
            JSONObject transaction = (JSONObject) eventObject.get("transaction");
            if (eventObject.get("engine_result").equals("tesSUCCESS") && transaction.get("TransactionType").equals("NFTokenBurn")) {

                String nfTokenId = ResponseParser.getValueFromJsonKeypath(eventObject,"transaction.NFTokenID");
                String issuer = determineIssuer(nfTokenId);
                if( issuer.equals("razjF3YgtuG7VZvDLNz4bsaHuHksmp6igG")) {

                    System.out.println("This is a ZerpCraft NFT");
                    System.out.println("Burning land");
                    new NFTBurnTask(nfTokenId).runTaskLaterAsynchronously(ZerpCraft.p, 0);
                }
            }
            if (eventObject.get("engine_result").equals("tesSUCCESS") && transaction.get("TransactionType").equals("NFTokenAcceptOffer")) {

                String NFTokenID = determineIDOfMovedNFT(ResponseParser.getArrayFromJsonKeypath(eventObject, "meta.AffectedNodes"));
                if (NFTokenID != null) {

                    String issuer = determineIssuer(NFTokenID);
                    System.out.println(issuer);

                    if( issuer.equals("razjF3YgtuG7VZvDLNz4bsaHuHksmp6igG")) {

                        System.out.println("This is a ZerpCraft NFT");
                        System.out.println("Calling Land Administrator");
                        new NFTTransferEventTask(eventObject).runTaskLaterAsynchronously(ZerpCraft.p, 0);
                    }
                }
            }
        }
        catch (ParseException e) {

            throw new RuntimeException(e);
        }
    }

    public String determineIssuer(String NFTokenId) {

        String issuerSegment = NFTokenId.substring(8,49);
        System.out.println(issuerSegment);
        byte[] addressBytes =  hexToBytes(issuerSegment);
        UnsignedByteArray addressToBeEncoded = UnsignedByteArray.of(addressBytes);
        AddressCodec addressCodec = new AddressCodec();

        return addressCodec.encodeAccountId(addressToBeEncoded).toString();
    }

    public String determineIDOfMovedNFT(JSONArray affectedNodes) {

        for (Object node : affectedNodes) {

            JSONObject jNode = (JSONObject) node;
            if (jNode.get("DeletedNode") != null && ResponseParser.getValueFromJsonKeypath(jNode,"DeletedNode.LedgerEntryType").equals("NFTokenOffer")) {

                return ResponseParser.getValueFromJsonKeypath(jNode, "DeletedNode.FinalFields.NFTokenID");
            }
        }
        return null;
    }

    public void addMessageHandler(MessageHandler msgHandler) {

        this.handler = msgHandler;
    }

    public static byte[] hexToBytes(String hex) {

        byte[] byteArray = new byte[hex.length() / 2];
        for (int i = 0; i < byteArray.length; i++) {

            int index = i * 2;
            int val = Integer.parseInt(hex.substring(index, index + 2), 16);
            byteArray[i] = (byte) val;
        }
        return byteArray;
    }

    public void sendMessage(String message) {

        this.session.getAsyncRemote().sendText(message);
    }
}
