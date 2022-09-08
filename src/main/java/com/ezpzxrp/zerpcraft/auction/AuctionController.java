package com.ezpzxrp.zerpcraft.auction;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.XUMM.XUMMController;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.database.IDatabaseManager;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

import static java.time.LocalTime.now;

public class AuctionController extends Throwable {

    public static void handleBid(Player player, String bid, boolean anon) throws AuctionController {

        int bidAmount;
        if (bid.equals("minimum")) {

            bidAmount = ZerpCraft.p.currentHighestBid + ZerpCraft.p.currentBidInterval;
        }
        else {
            try {

                bidAmount = Integer.parseInt(bid);
            }
            catch (Exception e) {

                throw new AuctionController("Invalid bid");
            }
            if (ZerpCraft.p.currentHighestBid + ZerpCraft.p.currentBidInterval > bidAmount) {

                throw new AuctionController("Bid too low");
            }
        }
        if (player == Bukkit.getPlayer(ZerpCraft.p.currentHighestBidder)) {

            throw new AuctionController("Bidder already winning");
        }
        if (UserManager.getPlayer(player).getProfile().getXummToken() == null) {

            throw new AuctionController("Player not registered");
        }
        double currentPlayerXRPBalance = DataHelper.getWalletBalanceByPlayerMinusReserves(player);
        if (currentPlayerXRPBalance < bidAmount) {

            throw new AuctionController("Insufficient funds");
        }
        if (ZerpCraft.p.currentHighestBid >= bidAmount) {

            throw new AuctionController("Race condition");
        }
        ZerpCraft.p.currentHighestBid = bidAmount;
        ZerpCraft.p.currentHighestBidder = player.getName();
        int nextMinimumValidBid = bidAmount + ZerpCraft.p.currentBidInterval;
        IDatabaseManager auctionManager = ZerpCraft.getDatabaseManager();
        auctionManager.recordAuctionBid(player.getName() + " " +  bidAmount + " " + now());
        if (anon) {

            Bukkit.broadcastMessage("New bid! The current bid sits at " + bidAmount + " held by an anonymous bidder! Next minimum bid is " + nextMinimumValidBid);
        }
        else {

            Bukkit.broadcastMessage("New bid! The current bid sits at " + bidAmount + " held by " + player.getName() + ". Next minimum bid is " + nextMinimumValidBid);
        }
    }

    public static boolean goingOnce(int bidAtCountDownStart) throws InterruptedException {

        Bukkit.broadcastMessage(ZerpCraft.p.currentHighestBid + " Going once!!!!!");
        Thread.sleep(3000L);
        if (ZerpCraft.p.currentHighestBid != bidAtCountDownStart) {
            return false;
        }
        return true;
    }

    public static boolean goingTwice(int bidAtCountDownStart) throws InterruptedException {

        Bukkit.broadcastMessage(ZerpCraft.p.currentHighestBid + " Going twice!!!!!");
        Thread.sleep(5000L);
        if (ZerpCraft.p.currentHighestBid != bidAtCountDownStart) {
            return false;
        }
        return true;
    }

    public static boolean goingThrice(int bidAtCountDownStart) throws InterruptedException {

        Bukkit.broadcastMessage(ZerpCraft.p.currentHighestBid + " Going three timeeeesssssss!!!!!");
        Thread.sleep(7000L);
        if (ZerpCraft.p.currentHighestBid != bidAtCountDownStart) {
            return false;
        }
        return true;
    }

    public static void requestPaymentFromAuctionWinner(String winnerName, int amount) {

        UUID playerUUID = Bukkit.getOfflinePlayer(winnerName).getUniqueId();
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);
        try {

            XUMMController.requestHandler(zcPlayer, amount);
        }
        catch (Exception | XUMMController ignored) {}
    }

    public AuctionController(String errorMessage) {

        super(errorMessage);
    }
}
