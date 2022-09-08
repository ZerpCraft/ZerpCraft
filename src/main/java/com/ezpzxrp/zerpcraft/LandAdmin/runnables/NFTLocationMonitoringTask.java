package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.XRPL.XRPLWebsocketClient;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.CountDownLatch;

public class NFTLocationMonitoringTask extends BukkitRunnable {

    public NFTLocationMonitoringTask() {}

    public void run() {

        System.out.println("Im in the runnable");
        final XRPLWebsocketClient client = new XRPLWebsocketClient("wss://xls20-sandbox.rippletest.net:51233");

        client.addMessageHandler(new XRPLWebsocketClient.MessageHandler() {
            public void handleMessage(String message) {
                //System.out.println(message);
            }
        });
        client.sendMessage("{\"id\": \"Example watch for new validated ledgers\",\"command\": \"subscribe\",\"streams\": [\"transactions\"]}");
        ZerpCraft.p.websocketLatch = new CountDownLatch(1);

        try {

            ZerpCraft.p.websocketLatch.await();
        }
        catch (InterruptedException e) {

            throw new RuntimeException(e);
        }

        System.out.println("Am I done?");
    }
}
