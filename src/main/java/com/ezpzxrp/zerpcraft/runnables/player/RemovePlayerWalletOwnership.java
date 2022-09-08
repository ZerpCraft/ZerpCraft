package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.XRPL.XRPLService;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.XUMM.XUMMController;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.datatypes.worldguard.Utilities;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;

import java.util.Map;

public class RemovePlayerWalletOwnership extends BukkitRunnable {

    private final ZerpCraftPlayer zcPlayer;

    public RemovePlayerWalletOwnership(ZerpCraftPlayer zcPlayer) {

        this.zcPlayer = zcPlayer;
    }

    @Override
    public void run() {

        //Loop through all regions and remove members/owners
        Map<String, ProtectedRegion> regionMap = Utilities.getWGRegionsByWallet(zcPlayer.getProfile().getXrplAddress());
        for (String regionId : regionMap.keySet()) {

            Utilities.removePlayersFromRegion(regionMap.get(regionId));
        }
        PlayerProfile profile = zcPlayer.getProfile();
        profile.setXrplAddress(null);
        profile.setXummToken(null);
        profile.setChanged();
    }
}
