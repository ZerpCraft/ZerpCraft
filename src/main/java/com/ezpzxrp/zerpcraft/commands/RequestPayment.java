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

import java.util.UUID;

public class RequestPayment implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;
        if (args.length == 0 || args.length == 1) {

            player.sendMessage("[ERROR] Missing arguments. Format is /zcSend [playerName] [amount]");
            return false;
        }
        UUID playerUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
        double paymentAmount = Double.parseDouble((args[1]));
        try {

            XUMMController.requestHandler(zcPlayer, paymentAmount);
        }
        catch (XUMMController e) {

            if(e.getMessage().equals("Expired registration link")) {

                player.sendMessage("It looks like your registration has expired. use the link below to re-register");
                new RegistrationTask(player).runTaskLaterAsynchronously(ZerpCraft.p,0);

            }
        }
        return true;
    }
}
