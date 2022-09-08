package com.ezpzxrp.zerpcraft.datatypes.worldguard;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class Utilities {
/*    public static ProtectedRegion getWGRegion(XLS20 nft) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getServer().getWorld("world_1640738270")));

        BlockVector3 position = BlockVector3.at(nft.getX1(), 0, nft.getZ1());
        try {

            ApplicableRegionSet set = regions.getApplicableRegions(position);
            for(ProtectedRegion region: set) {

                String regionId = region.getId();
*//*                regionId =
                if()*//*
            }
        }
        catch (NullPointerException e) {
            return null;
        }


    }*/

    public static ProtectedRegion getWGRegion(String regionId) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getServer().getWorld("world_1640738270")));
        return regions.getRegion(regionId);

    }

    public static ProtectedRegion getWGRegion(XLS20 nft) {

        World world = Bukkit.getServer().getWorld("world_1640738270");
        Location loc = new Location(world, nft.getX1(), 64, nft.getZ1());
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));
//        String flagValue = (String) set.queryValue(null, ZerpCraft.p.NFT_ID_FLAG);
        for (ProtectedRegion region : set) {

            String nftIdFlag = region.getFlag(ZerpCraft.p.NFT_ID_FLAG);
            if (nftIdFlag == null) {
                return null;
            }
            else if (nftIdFlag.equals(nft.getTokenId())) {

                return region;
            }
        }
        for (ProtectedRegion region : set) {

            Boolean isZerpCraftFlag = region.getFlag(ZerpCraft.p.IS_ZERPCRAFT_FLAG);
            String nftIdFlag = (String) region.getFlag(ZerpCraft.p.NFT_ID_FLAG);
            if (isZerpCraftFlag && nftIdFlag == null ) {

                return region;
            }
        }
        return null;
    }

    public static Map<String, ProtectedRegion> getWGRegionsByWallet(String walletId) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getServer().getWorld("world_1640738270")));
        Map<String, ProtectedRegion> regionMap = regions.getRegions();
        Map<String, ProtectedRegion> walletRegionMap = new HashMap<String, ProtectedRegion>();
        for (String regionId : regionMap.keySet()) {

            ProtectedRegion region = regionMap.get(regionId);
            String regionWalletId = region.getFlag(ZerpCraft.p.WALLET_ADDRESS_FLAG);

            if (regionWalletId == null || !regionWalletId.equals(walletId)) {

                continue;
            }
            walletRegionMap.put(regionId, region);
        }

        return walletRegionMap;
    }

    public static void removePlayersFromRegion(ProtectedRegion region) {

        DefaultDomain members = region.getMembers();
        members.removeAll();
        DefaultDomain owners = region.getOwners();
        owners.removeAll();
    }

    public static void addRegion(ProtectedRegion region) {

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getServer().getWorld("world_1640738270")));
        regions.addRegion(region);
    }
}
