package com.ezpzxrp.zerpcraft.commands;


import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;


public class ProvisionNFTs implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }

        UUID playerUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
        zcPlayer.setHasNewNFT(true);
        zcPlayer.setHasNFT(true);
        zcPlayer.setNFTCount(5);

        sender.sendMessage("Player " + args[0] + " should have their land provisioned on the next Land Admin cycle");

        return true;
    }
}
