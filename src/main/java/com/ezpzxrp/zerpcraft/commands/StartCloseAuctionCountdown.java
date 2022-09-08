package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.auction.AuctionController;
import com.ezpzxrp.zerpcraft.runnables.player.AuctionCountdownTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StartCloseAuctionCountdown implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        boolean auctionFinished;
        if (!sender.isOp()) {

            return true;
        }

        new AuctionCountdownTask().runTaskAsynchronously(ZerpCraft.p);
        return true;
    }
}
