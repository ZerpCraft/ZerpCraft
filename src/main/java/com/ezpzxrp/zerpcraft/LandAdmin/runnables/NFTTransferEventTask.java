package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.LandAdmin.LandController;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class NFTTransferEventTask extends BukkitRunnable {

    public final JSONObject tx;


    public NFTTransferEventTask(JSONObject tx) {

        this.tx = tx;
    }

    public void run() {

        JSONObject transferEvent = LandController.parseNFTokenOfferAcceptTxToTransferEvent(tx);
        LandController.orchestrateLandTransfer(transferEvent);
    }
}
