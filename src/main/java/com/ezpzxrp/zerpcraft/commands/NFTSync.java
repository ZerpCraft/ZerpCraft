package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.LandAdmin.runnables.NFTPlayerResyncTask;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NFTSync implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        new NFTPlayerResyncTask(zcPlayer).runTaskLaterAsynchronously(ZerpCraft.p, 0);

        return true;
    }
}
