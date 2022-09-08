package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetAuctionBidThreshold implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }
        int nextMinimumValidBid = ZerpCraft.p.currentHighestBid  + ZerpCraft.p.currentBidInterval;
        if (Integer.parseInt(args[0]) == 0) {

            sender.sendMessage("The current bid sits at " + ZerpCraft.p.currentHighestBid + " held by " + ZerpCraft.p.currentHighestBidder + ". Next minimum bid is  + nextMinimumValidBid");
        }
        else if (Integer.parseInt(args[0]) == 1) {

            Bukkit.broadcastMessage("New bid! The current bid sits at " + ZerpCraft.p.currentHighestBid + ". Next minimum bid is " + nextMinimumValidBid);
        }

        return true;
    }
}
