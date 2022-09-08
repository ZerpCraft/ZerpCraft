package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ToggleLandSale implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            if (args[0].equals("true") || args[0].equals("open") ) {

                ZerpCraft.p.openLandSale = true;
                sender.sendMessage("Toggle completed");
                sender.sendMessage(String.valueOf(ZerpCraft.p.openLandSale));
            }
            else {

                ZerpCraft.p.openLandSale = false;
                sender.sendMessage("Toggle completed");
                sender.sendMessage(String.valueOf(ZerpCraft.p.openLandSale));
            }
        }
        return true;
    }
}