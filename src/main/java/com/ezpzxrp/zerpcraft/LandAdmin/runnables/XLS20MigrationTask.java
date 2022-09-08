package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XLS20Devnet;
import com.ezpzxrp.zerpcraft.callouts.XRPLCluster;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS19;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

public class XLS20MigrationTask extends BukkitRunnable {

    ZerpCraftPlayer zcPlayer;

    public XLS20MigrationTask(ZerpCraftPlayer zcPlayer) {

        this.zcPlayer = zcPlayer;
    }

    public void run() {

        XRPLCluster publicNode = new XRPLCluster();
        JSONObject responseObject = publicNode.getWalletAccountInfo(zcPlayer.getProfile().getXrplAddress());
        String xls19NftDomain = ResponseParser.decodeDomain(responseObject);

        if(xls19NftDomain.equals("") || xls19NftDomain.equals("@xnft:\n" + "zc:\n[]")) {

            //Display wallet address here
            zcPlayer.getPlayer().sendMessage("Your XRP wallet's domain field is blank. If you believe this is in error, please contact the ZerpCraft Support Discord channel.");
            return;
        }

        String theString = xls19NftDomain.substring(xls19NftDomain.indexOf('[') + 1, xls19NftDomain.indexOf(']'));
        System.out.println(theString);
        String[] splittedString = theString.substring(0, theString.length()-1).split(",");
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(zcPlayer.getPlayer().getWorld()));
        String wgRegionId;

        for (String nftHash : splittedString) {

            System.out.println(nftHash);
            XLS19 nft = new XLS19(nftHash);
            zcPlayer.unlockPlots();
            wgRegionId = nft.convertToRegion();
            ProtectedRegion region =  regions.getRegion(wgRegionId);
            if (region == null) {

                zcPlayer.getPlayer().sendMessage("NFT string " + wgRegionId + " doesn't appear to be a valid ZerpCraft region. Please contact the ZerpCraft support channel on Discord with a screenshot of this message");
                continue;
            }
            // Push request out to XUMM
            XLS20 newNFT = new XLS20(nft);
            XUMM xumm = ZerpCraft.getXumm();
            try {
                String sellOfferId = newNFT.getSellOffer(wgRegionId);
                if (sellOfferId.isEmpty() || sellOfferId.isBlank()) {
                    throw new Exception();
                }
                xumm.nfTokenAcceptRequest(sellOfferId, zcPlayer.getProfile().getXummToken(), zcPlayer.getProfile().getXrplAddress());
            } catch ( Exception e) {
                zcPlayer.getPlayer().sendMessage("Your token " + wgRegionId + " failed to migrate. Please screenshot this error and paste in Discord support.");
                continue;
            }
        }
    }
}
