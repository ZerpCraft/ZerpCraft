package com.ezpzxrp.zerpcraft.util.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.google.common.collect.ImmutableList;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;

public class UserManager {

    private static HashSet<ZerpCraftPlayer> playerDataSet;
    private UserManager() {}

    public static void track(ZerpCraftPlayer zcPlayer) {

        System.out.println("The tracking is happening");
        zcPlayer.getPlayer().setMetadata(ZerpCraft.playerDataKey, new FixedMetadataValue(ZerpCraft.p, zcPlayer));
        if(playerDataSet == null) {

            playerDataSet = new HashSet<>();
        }
        playerDataSet.add(zcPlayer);
    }

    public static ZerpCraftPlayer getPlayer(Player player) {


        if (player != null && player.hasMetadata(ZerpCraft.playerDataKey)) {

            return (ZerpCraftPlayer) player.getMetadata(ZerpCraft.playerDataKey).get(0).value();
        }
        else {

            return null;
        }
    }

    public static void remove(Player player) {

        ZerpCraftPlayer zcPlayer = getPlayer(player);
        player.removeMetadata(ZerpCraft.playerDataKey, ZerpCraft.p);
        playerDataSet.remove(zcPlayer);
    }

    public static void saveAll() {

        if(playerDataSet == null) {

            return;
        }
        ImmutableList<ZerpCraftPlayer> trackedSyncData = ImmutableList.copyOf(playerDataSet);
        ZerpCraft.p.getLogger().info("Saving ZerpCraftPlayer profiles......" + trackedSyncData.size() + ")");
        for(ZerpCraftPlayer playerData : trackedSyncData) {

            try {

                ZerpCraft.p.getLogger().info("Saving data for player: " + playerData.getPlayerName());
                playerData.save(true);
            }
            catch (Exception e) {

                ZerpCraft.p.getLogger().info("Could not save ZerpCraftPlayer data for player: " + playerData.getPlayerName());
            }
            ZerpCraft.p.getLogger().info("Finished save");
        }
    }

    public static void clearAll() {
        for (Player player : ZerpCraft.p.getServer().getOnlinePlayers()) {
            remove(player);
        }

        if(playerDataSet != null)
            playerDataSet.clear(); //Clear sync save tracking
    }

    public static boolean hasPlayerDataKey(Entity entity) {

        return entity != null && entity.hasMetadata(ZerpCraft.playerDataKey);
    }
}
