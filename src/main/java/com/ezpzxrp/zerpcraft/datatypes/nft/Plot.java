package com.ezpzxrp.zerpcraft.datatypes.nft;

import com.ezpzxrp.zerpcraft.LandAdmin.WorldUtils;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.worldguard.RealmZC;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.UUID;

public class Plot {

    private String publicAddress;
    private String nftHash;
    private String realmName;
    private String worldName;
    private RealmZC realm;
    private boolean isLocked;
    private int xCoordinate1;
    private int zCoordinate1;
    private int xCoordinate2;
    private int zCoordinate2;
    private String regionId;
    private final World world;
    private final RegionManager regions;
    private XLS19 nft;

    public Plot(String publicAddress, XLS19 nft, UUID playerUUID) {

        this.publicAddress = publicAddress;
        this.nft = nft;
        nftHash = nft.getHash();
        loadHashData(nft);
        lock();
        world = Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        regions = container.get(worldEdit);
        initializeRealm(playerUUID);
    }

    public void lock() {

        this.isLocked = true;
    }

    public void unlock() {

        this.isLocked = false;
    }

    public boolean getIsLocked() {

        return this.isLocked;
    }

    public String getHash() {

        return this.nftHash;
    }

    public String getRegionId() {

        return this.regionId;
    }

    private void loadHashData(XLS19 nft) {


        this.xCoordinate1 = nft.getxCoordinate();
        this.xCoordinate2 = nft.getXCoordinate();
        this.zCoordinate1 = nft.getzCoordinate();
        this.zCoordinate2 = nft.getZCoordinate();
        this.worldName = nft.getWorldName();
    }

    public void initializeRealm(UUID playerUUID) {

        // Probably a lot of this initialization should be pulled up to the ZerpCraft class. Sticking it here for PoC
        // TODO: Initialize these objects in the main class



        int[] worldYAxis = WorldUtils.getYAxisByServerVersion(ZerpCraft.p.getRunningVersion());

        BlockVector3 min = BlockVector3.at(this.xCoordinate1, worldYAxis[0], this.zCoordinate1);
        BlockVector3 max = BlockVector3.at(this.xCoordinate2, worldYAxis[1], this.zCoordinate2);
        regionId =  nft.getPrefixFlag() + nft.getIssuanceNumber() + "x" + this.xCoordinate1 +  "z" + this.zCoordinate1 + "X" + this.xCoordinate2 + "Z" + this.zCoordinate2;

        if (regions.getRegion(regionId) != null) {

            return;
        }
        else {

            ProtectedRegion region = new ProtectedCuboidRegion(regionId, min, max);
            DefaultDomain members = region.getMembers();
            members.addPlayer(playerUUID);
            region.setOwners(members);
            region.setPriority(10);
            region.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
            region.setFlag(Flags.DENY_MESSAGE, "This land is owned by someone else!");

            regions.addRegion(region);
        }
    }

    public void deconstructRealm(String regionId) {

        System.out.println(this.regions.getRegion(regionId));
        System.out.println("am i getting to remove the region?");
        this.regions.removeRegion(regionId);
    }
}