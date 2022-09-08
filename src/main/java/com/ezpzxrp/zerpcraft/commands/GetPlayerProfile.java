package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XUMM.RegistrationTask;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerProfileTask;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GetPlayerProfile implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        UUID playerUUID = null;

        if(sender.isOp() && args.length >= 1) {

                OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
                playerUUID = player.getUniqueId();
                System.out.println(playerUUID);
        }
        else {
            Player player = (Player) sender;
            playerUUID = player.getUniqueId();
            System.out.println(playerUUID);
        }

        Player senderPlayer = (Player) sender;
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
        new PlayerProfileTask(zcPlayer, senderPlayer).runTaskAsynchronously(ZerpCraft.p);
        return true;
    }
}
