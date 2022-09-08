package com.ezpzxrp.zerpcraft.listener;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerGamemodeCreativeTask;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerProfileLoadingTask;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {


    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        ZerpCraft.p.getLogger().info("Player " + player.getName() + " joined. Loading profile....");

        new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(ZerpCraft.p, 0);
        if (player.hasPermission("group." + "registered")) {

            new PlayerGamemodeCreativeTask(player).runTask(ZerpCraft.p);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        if (!UserManager.hasPlayerDataKey(player)) {

            System.out.println("Not has player data key");
            return;
        }
        ZerpCraftPlayer zcPlayer = UserManager.getPlayer(player);
        if(zcPlayer == null) {

            System.out.println("null");
            return;
        }
        ZerpCraft.p.registeredPlayers.put(zcPlayer.getUuid(), zcPlayer);
        zcPlayer.logout(true);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public static void onPlayerCommandPreprocess(final PlayerCommandPreprocessEvent event) {

        final Player player = event.getPlayer();
        if(player.isOp()) {

            return;
        }
        final String[] args = event.getMessage().split(" ");
        final String start = args[0].toLowerCase();
        System.out.println(start);

        if (start.equals("/fill")) {
            if (args.length > 7) {

                if(args.length > 8) {
                    if(args[8].equalsIgnoreCase("destroy")) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Please use the REPLACE option instead of DESTROY");
                        return;
                    }
                }

                try {

                    System.out.println("Met the length criteria");
                    com.sk89q.worldedit.util.Location weLoc = BukkitAdapter.adapt(player.getLocation());
                    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
                    RegionQuery query = container.createQuery();
                    ApplicableRegionSet set = query.getApplicableRegions(weLoc);
                    for(ProtectedRegion region : set.getRegions()) {

                        DefaultDomain owners = region.getOwners();
                        if(owners.contains(player.getUniqueId())) {

                            System.out.println("Player is an owner");
                            BlockVector3 maxPoint = region.getMaximumPoint();
                            BlockVector3 minPoint = region.getMinimumPoint();

                            if(maxPoint.getBlockX() - Integer.parseInt(args[1]) >= 0 && maxPoint.getBlockZ() - Integer.parseInt(args[3]) >= 0
                                && maxPoint.getBlockX() - Integer.parseInt(args[4]) >= 0 && maxPoint.getBlockZ() - Integer.parseInt(args[6]) >= 0
                                && Integer.parseInt(args[1]) - minPoint.getBlockX() >= 0 && Integer.parseInt(args[3]) - minPoint.getBlockZ() >= 0
                                && Integer.parseInt(args[4]) - minPoint.getBlockX() >= 0 &&  Integer.parseInt(args[6]) - minPoint.getBlockZ() >= 0) {

                                return;
                            }
                        }
                    }
                }
                catch (Exception e) {

                    e.printStackTrace();
                    player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Something broke. Make sure you're using /fill in a single region you own. You also need to be standing in that region");
                    event.setCancelled(true);
                    return;
                }
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Command aborted. Make sure you're using /fill in a single region you own. You also need to be standing in that region");
                event.setCancelled(true);
            }
        }
    }
}