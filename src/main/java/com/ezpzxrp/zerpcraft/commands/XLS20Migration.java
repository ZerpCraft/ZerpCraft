package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.LandAdmin.runnables.XLS20MigrationTask;
import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerPurchaseLandTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;


public class XLS20Migration implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!sender.hasPermission("zerpcraft.zcXLS20")) {

            return false;
        }
        Player player = (Player) sender;
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());

        //check to make sure the player is registered
        if(zcPlayer == null) {

            player.sendMessage("You do not have a wallet registered. Register a wallet with /zcregister");
        }
        //needs to be an async process
        new XLS20MigrationTask(zcPlayer).runTaskLaterAsynchronously(ZerpCraft.p, 0);

        return true;
    }
}
