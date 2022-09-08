package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class NFTBurnTask extends BukkitRunnable {

    public final String nfTokenId;

    public NFTBurnTask(String nfTokenId) {

        this.nfTokenId = nfTokenId;
    }

    public void run() {

        World world = Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldEdit);
        Map<String, ProtectedRegion> regionMap = regions.getRegions();

        for (String key : regionMap.keySet()) {

            ProtectedRegion region = regionMap.get(key);
            String nftIdFlag = region.getFlag(ZerpCraft.p.NFT_ID_FLAG);
            if(nftIdFlag != null && nftIdFlag.equals(nfTokenId)) {

                regions.removeRegion(region.getId());
                System.out.println("I burned a region");
            }
        }
    }
}
