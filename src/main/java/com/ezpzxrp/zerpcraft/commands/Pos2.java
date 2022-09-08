package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerPurchaseLandTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class Pos2 implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        Location playerLocation = player.getLocation();

        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        zcPlayer.setPlayer(player);
        ZerpCraft.p.registeredPlayers.put(player.getUniqueId(), zcPlayer);
        System.out.println(zcPlayer.getPos1());
        player.sendMessage("Purchase point 2 set to coordinates (" + (int) Math.ceil(playerLocation.getX()) +  ", " +  (int) Math.ceil(playerLocation.getZ()) + ")");
        zcPlayer.setPos2(playerLocation);
        if (zcPlayer.getPos1() != null) {

            boolean purchasePermission = ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getPurchasePermission();
            if (!purchasePermission) {
                player.sendMessage(ChatColor.RED + "You cannot purchase at this time. Your purchase date is: " + ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getPurchaseDate());
                zcPlayer.setPos1(null);
                zcPlayer.setPos2(null);
                return true;
            }
            if (zcPlayer.getHasNewNFT()) {

                player.sendMessage(ChatColor.RED + "You cannot purchase at this time. Your previous purchase must be stamped to your wallet and registered to the server. Please sign your Account Set request and wait 90 seconds or until your plot is assigned");
                zcPlayer.setPos1(null);
                zcPlayer.setPos2(null);
                return true;
            }
            if (zcPlayer.getIsPurchasing()) {

                player.sendMessage("You already have an active purchase flow. You need to finish your previous purchase or wait a few minutes for the payment flow to expire");;
                zcPlayer.setPos1(null);
                zcPlayer.setPos2(null);
                return true;
            }

            Location location1 = zcPlayer.getPos1();
            ZerpCraft.p.lockedLand.put(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":" +
                    playerLocation.getBlockX() + ":" + playerLocation.getBlockZ(), LocalDateTime.now());
            zcPlayer.setIsPurchasing(true);
            new PlayerPurchaseLandTask(zcPlayer, zcPlayer.getPos1(), playerLocation).runTaskLaterAsynchronously(ZerpCraft.p, 0);
            zcPlayer.setPos1(null);
            zcPlayer.setPos2(null);
        }

        return true;
    }
}
