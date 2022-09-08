package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.auction.AuctionController;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AuctionCountdownTask extends BukkitRunnable {

    public AuctionCountdownTask() {}

    @Override
    public void run() {

        try {

            handleCountdown();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
    }

    public static boolean handleCountdown() throws InterruptedException {

        int bidAtCountDownStart = ZerpCraft.p.currentHighestBid;
        boolean continueAuctionCountdown;
        Bukkit.broadcastMessage("Auction countdown is starting!!! The current bid is " + ZerpCraft.p.currentHighestBid);
        Thread.sleep(1000L);
        continueAuctionCountdown = AuctionController.goingOnce(bidAtCountDownStart);
        if (continueAuctionCountdown) {

            continueAuctionCountdown = AuctionController.goingTwice(bidAtCountDownStart);
        }

        if (continueAuctionCountdown) {

            continueAuctionCountdown = AuctionController.goingThrice(bidAtCountDownStart);
        }
        if (!continueAuctionCountdown) {

            return false;
        }
        Bukkit.broadcastMessage("SOLD for " + ZerpCraft.p.currentHighestBid + " XRP to " + ZerpCraft.p.currentHighestBidder + "!!!!!");
        AuctionController.requestPaymentFromAuctionWinner(ZerpCraft.p.currentHighestBidder, ZerpCraft.p.currentHighestBid);


        ZerpCraft.p.isAuctionOpen = false;
        ZerpCraft.p.auctionWinner = ZerpCraft.p.currentHighestBidder;
        return continueAuctionCountdown;
    }
}