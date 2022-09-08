package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.UUID;

public class SetPurchasePermission implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
            UUID playerUUID = player.getUniqueId();
            ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);

            zcPlayer.getProfile().setPurchasePermission(Boolean.parseBoolean(args[1]));
            ZerpCraft.p.registeredPlayers.put(playerUUID, zcPlayer);
            sender.sendMessage("Purchase toggle successful");
        }
        return true;
    }
}