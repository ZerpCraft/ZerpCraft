package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OptIn implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        zcPlayer.getProfile().setP2POptIn(true);
        player.sendMessage("Your xrp wallet is now public. You can send + receive XRP from other players. To take your wallet private, use /zcOptOut");
        zcPlayer.getProfile().setChanged();

        zcPlayer = UserManager.getPlayer(player);
        zcPlayer.getProfile().setChanged();
        zcPlayer.getProfile().setP2POptIn(true);
        return true;
    }
}
