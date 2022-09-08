package com.ezpzxrp.zerpcraft.XUMM;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import jakarta.websocket.*;
import org.glassfish.tyrus.client.ClientManager;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@ClientEndpoint
public class XUMMWebSocketClient {

    // TODO: Run the socket connection on a different thread
    // TODO: have the onMessage poll untill it gets a "Right back at you" then wait 5 seconds

    private static CountDownLatch latch;
    private static Long startingTime;
    private static Long time;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private static Boolean isTimeout = false;
    int latchCounter = 0;
    Session session = null;
    private MessageHandler handler;

    public XUMMWebSocketClient(String signUUID){

        latch = new CountDownLatch(1);
        ClientManager client = ClientManager.createClient();

        try {

            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(XUMMWebSocketClient.class,new URI("wss://xumm.app/sign/" + signUUID));
            //client.asyncConnectToServer(XUMMWebSocketClient.class, new URI("wss://xumm.app/sign/" + signUUID));
            //latch.await();
        } catch (DeploymentException | URISyntaxException | IOException e) {

            throw new RuntimeException(e);
        }

        if (isTimeout) {

            client.shutdown();
            isTimeout = false;
            ZerpCraft.p.getLogger().info( "I did get into the timeout code");
            //return true;
        }

        client.shutdown();
        ZerpCraft.p.getLogger().info( "I signed successfully");
        //return false;
    }

    @OnOpen
    public void onOpen(Session session) {

        this.session = session;
        try {

            session.getBasicRemote().sendText("start");

        } catch (IOException e) {

            throw new RuntimeException(e);
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {

        handleWebsocketMessage(message, session);
    }

    public void handleWebsocketMessage(String message, Session session) {

        try {

            this.handler.handleMessage(message);
            JSONParser parser = new JSONParser();
            JSONObject responseObject = (JSONObject) parser.parse(message);
            ZerpCraft.p.getLogger().info( responseObject.toString());
            ZerpCraft.p.getLogger().info(String.valueOf(latch.getCount()));

            Boolean opened = (Boolean) responseObject.get("opened");
            Boolean signed = (Boolean) responseObject.get("signed");
            Boolean user_token = (Boolean) responseObject.get("user_token");
            time = (Long) responseObject.get("expires_in_seconds");
            if (startingTime == null && time != null) {

                startingTime = time;
            }
            else if(opened != null && opened) {

                ZerpCraft.p.getLogger().info("Opened!");
            }
            else if (signed != null && signed && user_token != null && user_token) {

                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Registration Complete"));
            }
            else if (time < (startingTime - 60)) {

                session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Request Timeout"));
            }
        } catch (ParseException | IOException | NullPointerException ignored) {}
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {

        if(closeReason.getReasonPhrase().equals("Request Timeout")) {

            isTimeout = true;
        }
        //latch.countDown();
        latchCounter -= 1;
    }

    public static interface MessageHandler {

        public void handleMessage(String message);
    }

    public void addMessageHandler(MessageHandler msgHandler) {
        this.handler = msgHandler;
    }

/*    public Boolean watchForASign() {

    }*/
}