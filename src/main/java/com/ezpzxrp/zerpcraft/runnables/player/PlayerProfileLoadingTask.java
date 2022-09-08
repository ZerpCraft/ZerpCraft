package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Date;
import java.util.Map;

public class PlayerProfileLoadingTask extends BukkitRunnable {

    private final Player player;

    public PlayerProfileLoadingTask(Player player) {

        this.player = player;
    }

    @Override
    public void run() {

        //Quit if the player is logged out
        if (!player.isOnline()) {

            ZerpCraft.p.getLogger().info("Aborting profile loading for " + player.getName());
            return;
        }

        PlayerProfile profile = ZerpCraft.getDatabaseManager().loadPlayerProfile(player.getName(), player.getUniqueId(), true);
        System.out.println(profile);
       if (profile != null) {

           try {

               ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(profile.getUniqueId());
               if (zcPlayer.getProfile().getPurchaseDate().before(new Date(System.currentTimeMillis()))) {

                   zcPlayer.getProfile().setPurchasePermission(true);
               }
               zcPlayer.setPlayer(player);
               new ApplySuccessfulProfile(zcPlayer).runTask(ZerpCraft.p);
               if(zcPlayer.getProfile().getXrplAddress() != null) {

                    Map<String, XLS20> nfts = zcPlayer.getNFTs();
                    //nftService.applyRolePermissions(nfts,zcPlayer);
               }
           }
           catch (Exception e) {

               new ApplySuccessfulProfile(new ZerpCraftPlayer(player, profile)).runTask(ZerpCraft.p);
           }
        }
        //UserManager.track(new ZerpCraftPlayer(player, profile));
    }

    private class ApplySuccessfulProfile extends BukkitRunnable {

        private final ZerpCraftPlayer zcPlayer;

        private ApplySuccessfulProfile(ZerpCraftPlayer tipperPlayer) {

            this.zcPlayer = tipperPlayer;
        }

        @Override
        public void run() {

            if (!player.isOnline()) {

                ZerpCraft.p.getLogger().info("Player is not online");
                return;
            }
            UserManager.track(zcPlayer);
            ZerpCraft.p.getLogger().info("Profile is loaded");
        }
    }
}
