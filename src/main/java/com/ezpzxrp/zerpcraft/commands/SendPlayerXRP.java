package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XUMM.RegistrationTask;
import com.ezpzxrp.zerpcraft.XUMM.XUMMController;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SendPlayerXRP implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {


        if(!sender.hasPermission("zerpcraft.send")) {

            sender.sendMessage("You do not hav permission to perform this command");
            return true;
        }
        Player player = (Player) sender;
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        if (!zcPlayer.getProfile().getP2POptIn()) {

            player.sendMessage(ChatColor.RED + "You need to opt in your wallet as public. Please perform /zcOptIn to confirm and try again.");
            return true;
        }
        if (args.length != 2) {

            player.sendMessage("[ERROR] Missing arguments. Format is /zcSend {playerName} {amount}");
            return true;
        }
        OfflinePlayer receivingPlayer = Bukkit.getOfflinePlayer(args[0]);
        UUID playerUUID = receivingPlayer.getUniqueId();
        ZerpCraftPlayer zcReceivingPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
        // What if they aren't registered at all?
        if(!zcReceivingPlayer.getProfile().getP2POptIn()) {

            player.sendMessage("The player you are trying to send to has not opted in their wallet. They will need to perform /zcOptIn");
            return true;
        }
        double paymentAmount = Double.parseDouble((args[1]));
        try {

            XUMMController.sendHandler(zcPlayer.getProfile(), paymentAmount, player, zcReceivingPlayer);
            player.sendMessage("Push request sent");
        }
        catch (XUMMController e) {

            if(e.getMessage().equals("Expired registration link")) {

                player.sendMessage("It looks like your registration has expired. use the link below to re-register");
                new RegistrationTask(player).runTaskLaterAsynchronously(ZerpCraft.p, 60);

            }
        }
        return true;
    }
}
