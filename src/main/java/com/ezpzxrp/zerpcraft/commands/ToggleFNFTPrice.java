package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ToggleFNFTPrice implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            ZerpCraft.p.FNFTPrice = Integer.parseInt(args[0]);
            sender.sendMessage("This worked. The FNFT Price is now " + args[0]);
        }
        return true;
    }
}