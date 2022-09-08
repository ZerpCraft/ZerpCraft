package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class SetAuctionBidIncrement implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }
        try {

            ZerpCraft.p.currentBidInterval = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {

            sender.sendMessage("Bad! Numbers only!");
            return true;
        }
        Bukkit.broadcastMessage("Bidding increment is now set to " + args[0] + " XRP");

        return true;
    }
}
