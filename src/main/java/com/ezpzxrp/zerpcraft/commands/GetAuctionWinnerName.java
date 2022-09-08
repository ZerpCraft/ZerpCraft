package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.auction.AuctionController;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GetAuctionWinnerName implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }
        sender.sendMessage(ZerpCraft.p.auctionWinner);
        return true;
    }
}
