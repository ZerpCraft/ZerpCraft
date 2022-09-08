package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;


public class getPlayerXUMMToken implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

            if (!sender.isOp()) {

                return true;
            }
            ZerpCraft.p.getLogger().info("Executing zcGetToken command");

            UUID playerUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
            System.out.println(playerUUID);
            ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
            System.out.println(zcPlayer);
            PlayerProfile profile = zcPlayer.getProfile();

            ZerpCraft.p.getLogger().info("Player " + args[0] + "'s XUMM token is " +  profile.getXummToken());
            sender.sendMessage("Player " + args[0] + "'s XUMM token is " +  profile.getXummToken());

            return true;
        }
}
