package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XUMM.RegistrationTask;
import com.ezpzxrp.zerpcraft.XUMM.XUMMController;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAuctionBidThreshold implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }
        try {

            ZerpCraft.p.currentHighestBid = Integer.parseInt(args[0]);
        }
        catch (NumberFormatException e) {

            sender.sendMessage("Bad! Numbers only!");
            return true;
        }
        Bukkit.broadcastMessage("Bidding is now set to " + args[0] + " XRP");

        return true;
    }
}
