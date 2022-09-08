package com.ezpzxrp.zerpcraft.listener;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerPurchaseFoundersNFTTask;
import com.ezpzxrp.zerpcraft.util.player.CheckHelper;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class PaymentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSignHit(PlayerInteractEvent event) {

        Player player = event.getPlayer();
        if (player.isOp()) {

            return;
        }
        Block block = event.getClickedBlock();

        if (player.isOp()) {

            assert block != null;
            if (block.getType().toString().equals("OAK_SIGN")) {

                boolean purchasePermission = ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getPurchasePermission();
                ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());

                player.sendMessage("Beginning Founders NFT payment flow");
                BlockState state = block.getState();
                Sign sign = (Sign)state;
                String signline1 = sign.getLine(0);
                int foundersNFTNumber;
                try {

                    foundersNFTNumber = Integer.parseInt(signline1);
                }
                catch (Exception e) {

                    zcPlayer.setIsPurchasing(false);
                    return;
                }
                boolean isPurchased = CheckHelper.isLandPurchased(foundersNFTNumber);
                if (isPurchased) {

                    player.sendMessage("That Founders NFT has already been purchased. Try to grab another one!");
                    zcPlayer.setIsPurchasing(false);
                    return;
                }
                boolean isLocked = CheckHelper.isLandLocked(foundersNFTNumber);
                if(isLocked) {

                    player.sendMessage("That Founders NFT is locked so another player can purchase it. Locks expire after 120 seconds.");
                    zcPlayer.setIsPurchasing(false);
                    return;
                }
                ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{true,false});
                System.out.println("Running Founders Payment Flow");
                boolean isRegistered = CheckHelper.checkIsRegistered(player);
                System.out.println("isRegistered: " + isRegistered);

                if (zcPlayer.getIsPurchasing()) {

                    player.sendMessage("You already have an active purchase flow. You need to finish your previous purchase or wait a few minutes for the payment flow to expire");
                    ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                    return;
                }
                if (zcPlayer.getHasNewNFT()) {

                    player.sendMessage(ChatColor.RED + "You cannot purchase at this time. Your previous purchase must be stamped to your wallet and registered to the server. Please sign your Account Set request and wait 90 seconds or until your plot is assigned");
                    ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                    return;
                }
                zcPlayer.setIsPurchasing(true);
                if (!purchasePermission) {
                    player.sendMessage("You will not have an opportunity to buy a Founders NFT until " + ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getPurchaseDate());
                    ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                    zcPlayer.setIsPurchasing(false);
                    return;
                }
                if(!isRegistered) {

                    player.sendMessage("You haven't registered your XUMM wallet. Use '/zcregister' before attempting to purchase");
                    zcPlayer.setIsPurchasing(false);
                    ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                    return;
                }

                if(ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getisFNFTOwner()) {

                    System.out.println("player is already an FNFT owner");
                    player.sendMessage("You already own a Founders NFT. Limit 1 per player/wallet. Consider buying some land during Open Sale with the money you saved.");
                    zcPlayer.setIsPurchasing(false);
                    ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                    return;
                }
                new PlayerPurchaseFoundersNFTTask(player, foundersNFTNumber, block.getX(), block.getY(), block.getZ()).runTaskLaterAsynchronously(ZerpCraft.p, 0);
            }
        }
    }
}
