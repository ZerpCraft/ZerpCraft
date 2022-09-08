package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class ToggleFNFT implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            Map<Integer, Boolean[]> fnftMap =  ZerpCraft.p.fNFTMap;

            for (int i = Integer.parseInt(args[1]); i <= Integer.parseInt( args[2]); i++) {

                Boolean[] lockArray = fnftMap.get(i);
                if (args[0].equals("lock")) {

                    lockArray[0] = true;
                }
                else {

                    lockArray[0] = false;
                }
                fnftMap.put(i,lockArray);
            }
            ZerpCraft.p.fNFTMap = fnftMap;
            sender.sendMessage("Toggle completed");
        }
        return true;
    }
}