package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.database.IDatabaseManager;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.ezpzxrp.zerpcraft.util.player.CheckHelper;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import static java.time.LocalTime.now;

public class PlayerPurchaseFoundersNFTTask extends BukkitRunnable {

    private final Player player;
    private final int foundersNFTNumber;
    private final int X;
    private final int Y;
    private final int Z;
    private final int FNFTCostInXRP;

    public PlayerPurchaseFoundersNFTTask(Player player, int foundersNFTNumber, int X, int Y, int Z ) {

        this.player = player;
        this.foundersNFTNumber = foundersNFTNumber;
        this.X = X;
        this.Y = Y;
        this.Z = Z;
        this. FNFTCostInXRP = ZerpCraft.p.FNFTPrice;
    }

    @Override
    public void run() {

        //Check for eligibility
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        boolean hasEnoughXRP = CheckHelper.checkHasSufficientXRP(player, 184);
        System.out.println("hasEnoughXRP: " + hasEnoughXRP);
        if(!hasEnoughXRP) {

            player.sendMessage("You don't have enough XRP in your XUMM wallet to purchase a Founders NFT. Try funding your wallet further");
            zcPlayer.setIsPurchasing(false);
            ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
            return;
        }
        boolean isFNFTOwner = false;
        try {

            isFNFTOwner = CheckHelper.isFNFTOwner(player);
        }
        catch (CheckHelper e) {

            if (e.getMessage().equals("Too Many NFTs"))  {

                player.sendMessage("You already own the maximum of 5 NFTs in your XRP wallet!");
                zcPlayer.setIsPurchasing(false);
                ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
                return;
            }
            System.out.println("Domain error for player " + player.getName());
            e.printStackTrace();
            player.sendMessage("It looks like you already have something in your wallet's Domain field. " +
                    "If you already own a ZerpCraft NFT, this will add your new purchase onto your existing NFTs. If you're using the Domain for another purpose, proceeding will overwrite your Domain field");
        }
        System.out.println("isFNFTOwner: " + isFNFTOwner);
        if(isFNFTOwner || ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getisFNFTOwner()) {

            player.sendMessage("You already own a Founders NFT. Limit 1 per player/wallet. Consider buying some land during Open Sale with the money you saved.");
            zcPlayer.setIsPurchasing(false);
            ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
            return;
        }

        //Make payment request
        XUMM xumm = ZerpCraft.getXumm();
        ZerpCraft.p.getLogger().info("Performing sign request");
        player.sendMessage("Performing payment request. You have 2 minutes to complete payment before the Founders NFT lock expires");
        String paymentResponse = xumm.paymentRequest("rZerpMcPjoRGzaScS86y8WuNQoRY8MwJG", FNFTCostInXRP ,zcPlayer.getProfile().getXummToken(), 2, zcPlayer.getProfile().getXrplAddress());

        //Listen for payment
        ZerpCraft.p.getLogger().info("Transforming into JSON");
        JSONObject signResponseJson = ResponseParser.stringToJSON(paymentResponse);

        String registrationUUID = ResponseParser.getValueFromJsonKeypath(signResponseJson, "uuid");
        ZerpCraft.p.getLogger().info("UUID is: " + registrationUUID);
        int count = 0;
        String userTokenResponse = null;
        Boolean signed = false;
        JSONObject tokenResponseJson = null;
        ZerpCraft.p.getLogger().info("Entering Founders NFT wait loop");
        while (count <= 120) {


            userTokenResponse = xumm.getUserTokenRequest(registrationUUID);
            tokenResponseJson = ResponseParser.stringToJSON(userTokenResponse);
            signed = ResponseParser.getBooleanFromJsonKeypath(tokenResponseJson,"meta.signed");
            count += 5;
            if(signed) {

                break;
            }
            try{

                Thread.sleep(5000);
            }
            catch(InterruptedException e) {

                zcPlayer.setIsPurchasing(false);
            }
        }
        if(count >= 120) {

            player.sendMessage("Payment request has expired. Please try again.");
            zcPlayer.setIsPurchasing(false);
            ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,false});
            return;
        }
        player.sendMessage("Thank you for your payment. You should now see a new 'Account Set' request. This is not a payment. Sign this transaction to stamp the Founders NFT to your wallet.");
        ZerpCraft.p.fNFTMap.put(foundersNFTNumber,new Boolean[]{false,true});
        ZerpCraftPlayer zcplayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        zcPlayer.setHasNFT(true);
        zcPlayer.setIsPurchasing(false);
        zcPlayer.setHasNewNFT(true);
        zcplayer.setIsFNFTOwner(true);
        ZerpCraft.p.registeredPlayers.put(player.getUniqueId(), zcplayer);

        IDatabaseManager purchaseManager = ZerpCraft.getDatabaseManager();
        purchaseManager.recordPurchase(player.getUniqueId() + " FXX " + foundersNFTNumber + " " +  FNFTCostInXRP + " " + now() + " "  + ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress() + " na " + player.getName());

        String coordinates = "";
        World world =  Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        com.sk89q.worldedit.util.Location loc = new com.sk89q.worldedit.util.Location( worldEdit, X, Y, Z);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(loc);
        for (ProtectedRegion region : set.getRegions()) {

            String Id = region.getId();
            System.out.println(Id);
            if (Id.equals("fxx" + String.format("%03d", foundersNFTNumber))) {

                BlockVector3 max = region.getMaximumPoint();
                BlockVector3 min = region.getMinimumPoint();
                System.out.println(max);
                System.out.println(min);
                coordinates = "x" + Integer.toString(max.getBlockX()) + "z" + Integer.toString(max.getBlockZ()) + "X" + Integer.toString(min.getBlockX()) + "Z" + Integer.toString(min.getBlockZ());
            }
        }
        String hash = DataHelper.addXLS19EntryToDomain(player,"FXX:" + String.format("%03d", foundersNFTNumber) + ":" + coordinates );
        int nftCount = DataHelper.countNFTsInDomain(hash);
        zcPlayer.setNFTCount(nftCount);
        System.out.println(hash);
        String stampResponse = xumm.stampNFT(hash, ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXummToken(), ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress());
    }

}
