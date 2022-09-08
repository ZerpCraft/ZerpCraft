package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PlayerProfileTask extends BukkitRunnable {

    ZerpCraftPlayer zcPlayer;
    Player senderPlayer;

    public PlayerProfileTask(ZerpCraftPlayer zcPlayer, Player senderPlayer) {

        this.zcPlayer = zcPlayer;
        this.senderPlayer = senderPlayer;
    }

    @Override
    public void run() {

        DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        //Date date;

        Player player = zcPlayer.getPlayer();
        String publicAddress = zcPlayer.getProfile().getXrplAddress();
        String purchaseDate = null;
        if(publicAddress == null) {

            publicAddress = "You have not registered your XUMM wallet. Use /zcRegister to get setup \n";
        }

        Boolean purchasePermission = zcPlayer.getProfile().getPurchasePermission();
        String purchasePermissionInfo = null;
        if(purchasePermission == false && publicAddress == null) {

            purchaseDate = ChatColor.RED + "" + zcPlayer.getProfile().getPurchaseDate();
            purchasePermissionInfo = "You do not yet have the ability to purchase. You need to register your XUMM wallet to get a purchase date assigned \n";
        }
        else if(purchasePermission == false) {

            purchaseDate = ChatColor.RED + "" + zcPlayer.getProfile().getPurchaseDate();
            purchasePermissionInfo = "You do not yet have the ability to purchase.\n";
        }
        else {

            purchasePermissionInfo = "You are allowed to purchase on the server. Use /zcpos1 followed by /zcpos2 to purchase an area of land \n";
            purchaseDate = ChatColor.GREEN + "NOW!!!";
        }

        if (senderPlayer.isOp()) {

            String link = "bithomp.com/explorer/" + publicAddress;
            senderPlayer.sendMessage(
                    "Player Profile: \n" +
                    "Wallet Link: " + link + "\n" +
                    "Purchase Permission: " + purchasePermissionInfo + "\n" +
                    "Purchase Date: " +  purchaseDate
            );
        }
        else {

            senderPlayer.sendMessage(
                    "Player Profile: \n" +
                    "Player Wallet Address: " + publicAddress + "\n" +
                    "Purchase Permission: " + purchasePermissionInfo + "\n" +
                    "Purchase Date: " +  purchaseDate
            );
        }
    }
}
