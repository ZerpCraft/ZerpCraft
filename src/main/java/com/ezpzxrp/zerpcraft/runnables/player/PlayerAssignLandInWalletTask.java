package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.LandAdmin.LandController;
import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class PlayerAssignLandInWalletTask extends BukkitRunnable {

    private ZerpCraftPlayer zcPlayer;
    JSONArray zerpCraftNFTs;

    public PlayerAssignLandInWalletTask(ZerpCraftPlayer zcPlayer, JSONArray zerpCraftNFTs) {

        this.zcPlayer = zcPlayer;
        this.zerpCraftNFTs = zerpCraftNFTs;
    }

    @Override
    public void run() {

        for(Object zcNFT : zerpCraftNFTs) {

            JSONObject zcNFTObject = (JSONObject) zcNFT;
            JSONObject transferEvent = DataHelper.convertNFTToTransferObject(zcNFTObject, zcPlayer.getProfile().getXrplAddress());
            LandController.orchestrateLandTransfer(transferEvent);
        }
    }
}
