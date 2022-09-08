package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.LandAdmin.LandController;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XLS20Devnet;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.Map;

public class XRPLNFTSyncTask extends BukkitRunnable {

    public XRPLNFTSyncTask() {}

    public void run() {

        World world = Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldEdit);
        Map<String, ProtectedRegion> regionMap = regions.getRegions();


        for (String key : regionMap.keySet()) {

            ProtectedRegion region = regionMap.get(key);
            boolean isZerpCraftRegion = false;
            try {

                isZerpCraftRegion = region.getFlag(ZerpCraft.p.IS_ZERPCRAFT_FLAG);
            }
            catch (NullPointerException ignored) {}
            if (isZerpCraftRegion) {

                try {

                    String nftId = region.getFlag(ZerpCraft.p.NFT_ID_FLAG);
                    String walletId = region.getFlag(ZerpCraft.p.WALLET_ADDRESS_FLAG);
                    XLS20Devnet node = new XLS20Devnet();
                    JSONObject response = node.getNFTInfo(nftId);
                    boolean isBurned = ResponseParser.getBooleanFromJsonKeypath(response, "result.is_burned");
                    if (isBurned) {

                        regions.removeRegion(region.getId());
                        System.out.println("I burned a region");
                    }
                    JSONObject nftEvent = LandController.parseNFTResponse(response);
                    if (!walletId.equals(nftEvent.get("DestinationWallet"))) {

                        LandController.orchestrateLandTransfer(nftEvent);
                    }
                }
                catch (Exception e) {
                    System.out.println("Exception while syncing NFTs");
                    e.printStackTrace();
                }
            }
        }
    }
}
