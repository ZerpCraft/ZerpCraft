package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.LandAdmin.LandController;
import com.ezpzxrp.zerpcraft.XRPL.XRPLService;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.datatypes.worldguard.Utilities;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


public class NFTPlayerResyncTask extends BukkitRunnable {

    public final ZerpCraftPlayer zcPlayer;

    public NFTPlayerResyncTask(ZerpCraftPlayer zcPlayer) {

        this.zcPlayer = zcPlayer;
    }

    public void run() {

        JSONArray zerpCraftNFTs = XRPLService.getZerpCraftNFTsFromWallet(zcPlayer);
        String walletId = zcPlayer.getProfile().getXrplAddress();
        for (Object zerpCraftNFT : zerpCraftNFTs) {

            JSONObject jZerpCraftNFT = (JSONObject) zerpCraftNFT;
            XLS20 nft = new XLS20(jZerpCraftNFT);
            ProtectedRegion region = Utilities.getWGRegion(nft);
            if (region == null || !region.getFlag(ZerpCraft.p.WALLET_ADDRESS_FLAG).equals(walletId)) {

                if (region != null ) {

                    System.out.println(region.getId());
                }
                System.out.println(nft.getTokenId());
                System.out.println(walletId);
                LandController.transferRegion(region, nft, walletId);
            }
        }
    }
}
