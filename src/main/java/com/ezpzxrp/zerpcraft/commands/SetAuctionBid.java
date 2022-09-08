package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.auction.AuctionController;
import com.ezpzxrp.zerpcraft.database.IDatabaseManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetAuctionBid implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        ZerpCraft.p.getLogger().info("Executing zcBid command");

        Player player = (Player) sender;
        try {

            boolean anon = false;
            if (args.length == 2) {
                if (args[1].equals("anon")) {

                    anon = true;
                }
            }
            AuctionController.handleBid(player, args[0], anon);
        }
        catch (AuctionController e) {

            if( e.getMessage().equals("Invalid bid")) {

                player.sendMessage("Invalid bid number. Please enter in a whole number or the text 'minimum'");
            }
            else if (e.getMessage().equals("Bid too low")) {

                int minimumValidBid = (ZerpCraft.p.currentHighestBid + ZerpCraft.p.currentBidInterval);
                player.sendMessage("Bid is too low. Please place a bid at " +  minimumValidBid + " XRP or higher" );
            }
            else if (e.getMessage().equals("Race condition")) {

                player.sendMessage("DRAT!! It looks like someone put in a higher bid before you. Try a bid higher than " + ZerpCraft.p.currentHighestBid);
            }
            else if (e.getMessage().equals("Bidder already winning")) {

                player.sendMessage("You're already winning this auction!");
            }
            else if (e.getMessage().equals("Player not registered")) {

                player.sendMessage("You're not registered! Use /zcregister to register your XUMM wallet with the server");
            }
            else if (e.getMessage().equals("Insufficient funds")) {

                player.sendMessage("You don't have enough XRP to make that bid!");
            }
            return false;
        }

        player.sendMessage("Your bid was successful.");
        return true;
    }
}
