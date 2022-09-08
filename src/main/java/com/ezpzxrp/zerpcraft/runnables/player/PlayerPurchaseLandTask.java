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
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;


import java.time.LocalDateTime;
import java.util.Map;

import static java.time.LocalTime.now;


public class PlayerPurchaseLandTask extends BukkitRunnable {

    private final ZerpCraftPlayer zcPlayer;
    private final Location location1;
    private final Location location2;
    private int landIncrement;
    private final XUMM xumm;
    World world;
    com.sk89q.worldedit.world.World worldEdit;
    RegionContainer container;
    RegionManager regions;

    public PlayerPurchaseLandTask(ZerpCraftPlayer zcPlayer, Location location1, Location location2) {

        this.zcPlayer = zcPlayer;
        this.location1 = location1;
        this.location2 = location2;
        this.xumm = ZerpCraft.getXumm();
    }

    @Override
    public void run() {
        Player player = zcPlayer.getPlayer();
        zcPlayer.setPos1(null);
        zcPlayer.setPos2(null);
        ZerpCraft.p.registeredPlayers.put(player.getUniqueId(), zcPlayer);
        String playersInputKey = player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                + location2.getBlockX() + ":" + location2.getBlockZ();
            //TODO: need to make sure the land minting number is updated after payment
            //Quit if the player is logged out

            if (!player.isOnline()) {

                ZerpCraft.p.getLogger().info("Aborting Land Purchase loading for " + player.getName() + " player not online");
                zcPlayer.setIsPurchasing(false);
                System.out.println("Dumping Locked Land");
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                return;
            }
            //Check if the player is registered
            boolean isRegistered = CheckHelper.checkIsRegistered(player);
            System.out.println("isRegistered: " + isRegistered);
            if (!isRegistered) {

                player.sendMessage("You haven't registered your XUMM wallet. Use '/zcregister' before attempting to purchase");
                zcPlayer.setIsPurchasing(false);
                System.out.println("Dumping Locked Land");
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                return;
            }
            player.sendMessage("Initiating land purchase");
            ZerpCraft.p.getLogger().info("Initiating land for " + player.getName() + " at coordinates (" + location1.getBlockX() + "," + location1.getBlockZ() + ")" +
                    " to " + location2.getBlockX() + ", " + location2.getBlockZ());

            // Grab outside points of each chunk
            ZerpCraft.p.getLogger().info("Pythagorizing");
            Location[] purchasePoints = pythagorizeTheShitOuttaThisBitch(location1, location2);

            // Calculate chunk area
            int a = (int) Math.ceil(Math.abs(purchasePoints[0].getBlockX() - purchasePoints[1].getBlockX()) / 16.0);
            int b = (int) Math.ceil(Math.abs(purchasePoints[0].getBlockZ() - purchasePoints[1].getBlockZ()) / 16.0);
            int landChunkArea = a * b;
            System.out.println(landChunkArea);
//            System.out.println(zcPlayer.getProfile().getPurchaseSize());
            if (landChunkArea > 5000) {

                player.sendMessage("Individual purchases greater than 5000 chunks are not available at this time." + " The size you selected was " +  landChunkArea  +  ". Please either resize your purchase selection or contact zerpcraftmc@gmail.com regarding OTC purchases");
                zcPlayer.setIsPurchasing(false);
                System.out.println("Dumping Locked Land");
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                return;
            }
            boolean hasEnoughXRP = true;
            try {
                hasEnoughXRP = CheckHelper.checkHasSufficientXRP(player, landChunkArea);
            }
            catch (Exception e) {

                System.out.println("Dumping Locked Land");
                player.sendMessage("There was an error checking your XRP balance, try again");
                zcPlayer.setIsPurchasing(false);
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                e.printStackTrace();
                return;
            }
            System.out.println("hasEnoughXRP: " + hasEnoughXRP);
            if (!hasEnoughXRP) {

                player.sendMessage("You don't have enough XRP in your XUMM wallet to purchase that land size. Try funding your wallet more or selecting an area smaller than " + landChunkArea + " chunks");
                zcPlayer.setIsPurchasing(false);
                System.out.println("Dumping Locked Land");
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                return;
            }
            System.out.println("Total chunk value calculated " + landChunkArea);

            // Create the unowned region
            if (ZerpCraft.p.openLandSale) {
                ZerpCraft.p.getLogger().info("Setting up region");
                //String regionId = landPurchaseRegionSetup(purchasePoints);

                // Here I need to check and make sure the region doesn't overlap with other areas? Or should I do that before the region is even created?
                try {

                    boolean isLandLocked = isLandLocked(purchasePoints, playersInputKey);
                    if (isLandLocked) {

                        player.sendMessage("It looks like this area has already been purchased, or another player is currently going through the purchase process. \n" +
                                "Please use /dyn to scout for areas of land not shaded in red. \n" +
                                "New purchases can take a few minutes to show up on the map");

                        System.out.println("Dumping Locked Land");
                        zcPlayer.setIsPurchasing(false);
                        System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                                + location2.getBlockX() + ":" + location2.getBlockZ());
                        ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                                + location2.getBlockX() + ":" + location2.getBlockZ());
                        return;
                    }
                } catch (Exception e) {

                    e.printStackTrace();
                    System.out.println("Dumping Locked Land");
                    zcPlayer.setIsPurchasing(false);
                    System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                            + location2.getBlockX() + ":" + location2.getBlockZ());
                    ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                            + location2.getBlockX() + ":" + location2.getBlockZ());
                    return;
                }
                // Payment Request
                String hash;
                try {

                    hash = DataHelper.addXLS19EntryToDomain(player, "LXX:" + landChunkArea + ":x" + purchasePoints[0].getBlockX() + "z" +
                            purchasePoints[0].getBlockZ() + "X" + purchasePoints[1].getBlockX() + "Z" + purchasePoints[1].getBlockZ());
                    if (hash.equals("Too Many NFTs")) {

                        System.out.println("Dumping Locked Land");
                        zcPlayer.setIsPurchasing(false);
                        System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                                + location2.getBlockX() + ":" + location2.getBlockZ());
                        ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                                + location2.getBlockX() + ":" + location2.getBlockZ());
                        player.sendMessage("You already own the maximum of 5 NFTs in your XRP wallet!");
                        return;
                    }
                    if (landChunkArea == 1) {
                        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "You are only purchasing 1 chunk of land. If this was a mistake, please decline the XUMM request. Tutorial on how to purchase land here: https://youtu.be/IpFv2bOVgEk");
                    }
                    performLandPurchaseRequest(landChunkArea);
                } catch (Exception e) {

                    System.out.println("Dumping Locked Land");
                    zcPlayer.setIsPurchasing(false);
                    System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                            + location2.getBlockX() + ":" + location2.getBlockZ());
                    ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                            + location2.getBlockX() + ":" + location2.getBlockZ());
                    return;
                }

                LocalDateTime ogTimeStamp = ZerpCraft.p.lockedLand.get(playersInputKey);
                System.out.println(ogTimeStamp);
                ZerpCraft.p.lockedLand.remove(playersInputKey);
                ZerpCraft.p.lockedLand.put(player.getUniqueId() + ":" + purchasePoints[0].getBlockX() + ":" + purchasePoints[0].getBlockZ() + ":"
                        + purchasePoints[1].getBlockX() + ":" + purchasePoints[1].getBlockZ(), ogTimeStamp);
                int nftCount = DataHelper.countNFTsInDomain(hash);
                player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Your current NFT count is " + nftCount + ". You have room for " + (5 - nftCount) + " left in your wallet!");
                /*String hash = DataHelper.addXLS19EntryToDomain(player, "LXX:" + landChunkArea + ":x" + purchasePoints[0].getBlockX() + "z" +
                        purchasePoints[0].getBlockZ() + "X" + purchasePoints[1].getBlockX() + "Z" + purchasePoints[1].getBlockZ());*/
                System.out.println(hash);

                IDatabaseManager purchaseManager = ZerpCraft.getDatabaseManager();
                purchaseManager.recordPurchase(player.getUniqueId() + " LXX " + landIncrement + " " + landChunkArea + " " + now() + " " + zcPlayer.getProfile().getXrplAddress() + " " +
                        "LXX:" + landIncrement + ":x" + purchasePoints[0].getBlockX() + "z" + purchasePoints[0].getBlockZ() + "X" + purchasePoints[1].getBlockX() + "Z" + purchasePoints[1].getBlockZ() + " " + player.getName());
                zcPlayer.setNFTCount(nftCount);
                zcPlayer.setHasNFT(true);
                zcPlayer.setIsPurchasing(false);
                zcPlayer.setHasNewNFT(true);
                ZerpCraft.p.registeredPlayers.put(player.getUniqueId(), zcPlayer);

                String stampResponse = xumm.stampNFT(hash, ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXummToken(),ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress());
            } else {

                System.out.println("Dumping Locked Land");
                zcPlayer.setIsPurchasing(false);
                System.out.println(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                ZerpCraft.p.lockedLand.remove(player.getUniqueId() + ":" + location1.getBlockX() + ":" + location1.getBlockZ() + ":"
                        + location2.getBlockX() + ":" + location2.getBlockZ());
                player.sendMessage( ChatColor.RED + "" + ChatColor.BOLD + "Land purchasing is temporarily locked. Probably the code is being updated. Please try again at a later date");
            }

    }

    public Location[] pythagorizeTheShitOuttaThisBitch(Location location1, Location location2) {

        World w = location1.getWorld();

        System.out.println("I'm pythagorizing!");

        Chunk chunk1 = location1.getChunk();
        Location[] chunkArray1 = new Location[4];
        chunkArray1[0] = new Location(w, chunk1.getX() * 16, -64, chunk1.getZ() * 16);
        chunkArray1[1] = new Location(w, chunk1.getX() * 16, -64, (chunk1.getZ() * 16) + 15);
        chunkArray1[2] = new Location(w, (chunk1.getX() * 16) + 15, -64, chunk1.getZ() * 16);
        chunkArray1[3] = new Location(w, (chunk1.getX() * 16) + 15, -64, (chunk1.getZ() * 16) + 15);

        Chunk chunk2 = location2.getChunk();
        Location[] chunkArray2 = new Location[4];
        chunkArray2[0] = new Location(w, chunk2.getX() * 16, 319, chunk2.getZ() * 16);
        chunkArray2[1] = new Location(w, chunk2.getX() * 16, 319, (chunk2.getZ() * 16) + 15);
        chunkArray2[2] = new Location(w, (chunk2.getX() * 16) + 15, 319, chunk2.getZ() * 16);
        chunkArray2[3] = new Location(w, (chunk2.getX() * 16) + 15, 319, (chunk2.getZ() * 16) + 15);

        double hypotenuse = 0;
        Location returnLocation1 = null;
        Location returnLocation2 = null;

        for (Location loc1 : chunkArray1) {
            for (Location loc2 : chunkArray2) {

                int a = Math.abs(loc1.getBlockX() - loc2.getBlockX());
                int b = Math.abs(loc1.getBlockZ() - loc2.getBlockZ());
                double c = Math.sqrt((a*a) + (b*b));
                if (c >= hypotenuse) {

                    hypotenuse = c;
                    returnLocation1 = loc1;
                    returnLocation2 = loc2;
                }
            }
        }

        return new Location[]{returnLocation1,returnLocation2};
    }

    public String landPurchaseRegionSetup(Location[] purchasePoints) {

        this.landIncrement = ZerpCraft.p.openLandIncrement;
        ZerpCraft.p.openLandIncrement++;
        BlockVector3 min = BlockVector3.at(purchasePoints[0].getBlockX(), purchasePoints[0].getBlockY(), purchasePoints[0].getBlockZ());
        BlockVector3 max = BlockVector3.at(purchasePoints[1].getBlockX(), purchasePoints[1].getBlockY(), purchasePoints[1].getBlockZ());
        String regionId = "LXX" + landIncrement + "x" + purchasePoints[0].getBlockX() + "z" +
                purchasePoints[0].getBlockZ() + "X" + purchasePoints[1].getBlockX() + "Z" + purchasePoints[1].getBlockZ();
        System.out.println(regionId);
        ProtectedRegion region = new ProtectedCuboidRegion(regionId, min, max);

        World world = purchasePoints[0].getWorld();
        this.worldEdit = BukkitAdapter.adapt(world);
        this.container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        this.regions = container.get(worldEdit);
        regions.addRegion(region);

        return regionId;
    }

    public void performLandPurchaseRequest(int priceInXRP) throws Exception {

        //Make payment request
        Player player = zcPlayer.getPlayer();
        ZerpCraft.p.getLogger().info("Performing sign request");
        player.sendMessage("Performing payment request. You have 5 minutes to complete payment before the land lock expires");
        String paymentResponse = xumm.paymentRequest("rZerpMcPjoRGzaScS86y8WuNQoRY8MwJG", priceInXRP ,zcPlayer.getProfile().getXummToken(), 5, zcPlayer.getProfile().getXrplAddress());

        //Listen for payment
        ZerpCraft.p.getLogger().info("Transforming into JSON");
        JSONObject signResponseJson = ResponseParser.stringToJSON(paymentResponse);

        String registrationUUID = ResponseParser.getValueFromJsonKeypath(signResponseJson, "uuid");
        ZerpCraft.p.getLogger().info("UUID is: " + registrationUUID);
        int count = 0;
        String userTokenResponse = null;
        Boolean signed = false;
        Boolean resolved = false;
        JSONObject tokenResponseJson = null;
        ZerpCraft.p.getLogger().info("Entering Land Purchase wait loop for player " + player.getName());
        while (count <= 300) {


            userTokenResponse = xumm.getUserTokenRequest(registrationUUID);
            tokenResponseJson = ResponseParser.stringToJSON(userTokenResponse);
            signed = ResponseParser.getBooleanFromJsonKeypath(tokenResponseJson,"meta.signed");
            resolved = ResponseParser.getBooleanFromJsonKeypath(tokenResponseJson,"meta.resolved");
            count += 10;
            if(signed && resolved) {

                break;
            }
            if (!signed && resolved) {
                player.sendMessage("Sign request canceled. Please try again.");
                throw new Exception();
            }
            try{

                Thread.sleep(10000);
            }
            catch(InterruptedException e) {}
        }
        if(count >= 300) {

            player.sendMessage("Payment request has expired and your land has been unlocked. Please try again.");
            throw new Exception();
        }
        player.sendMessage("Thank you for your payment. You should now see a new 'Account Set' request. This is not a payment. Sign this transaction to stamp the land to your wallet.");
    }

    public boolean isLandLocked(Location[] purchasePoints, String playersInputKey) {

        Map<String, LocalDateTime> lockedLand = ZerpCraft.p.lockedLand;
        World w = location1.getWorld();

        for(String key : lockedLand.keySet()) {

            if(key.equals(playersInputKey)) {

                continue;
            }
            String[] keyArray =  key.split(":");
            Location pythagorizedLockedLocation1 = new Location(w, Integer.parseInt(keyArray[1]), -64, Integer.parseInt(keyArray[2]));
            Location pythagorizedLockedLocation2 = new Location(w, Integer.parseInt(keyArray[3]), 319, Integer.parseInt(keyArray[4]));
            //Location[] pythagorizedLockedLocations = pythagorizeTheShitOuttaThisBitch(lockedLocation1, lockedLocation2);

            //Location pythagorizedLockedLocation1 = pythagorizedLockedLocations[0];
            //Location pythagorizedLockedLocation2 = pythagorizedLockedLocations[1];

            Location purchaseLocation1 = purchasePoints[0];
            Location purchaseLocation2 = purchasePoints[1];

            int rect1MinX = Math.min(purchaseLocation1.getBlockX(), purchaseLocation2.getBlockX());
            int rect1MaxX = Math.max(purchaseLocation1.getBlockX(), purchaseLocation2.getBlockX());
            int rect2MinX = Math.min(pythagorizedLockedLocation1.getBlockX(), pythagorizedLockedLocation2.getBlockX());
            int rect2MaxX = Math.max(pythagorizedLockedLocation1.getBlockX(), pythagorizedLockedLocation2.getBlockX());

            int rect1MinZ = Math.min(purchaseLocation1.getBlockZ(), purchaseLocation2.getBlockZ());
            int rect1MaxZ = Math.max(purchaseLocation1.getBlockZ(), purchaseLocation2.getBlockZ());
            int rect2MinZ = Math.min(pythagorizedLockedLocation1.getBlockZ(), pythagorizedLockedLocation2.getBlockZ());
            int rect2MaxZ = Math.max(pythagorizedLockedLocation1.getBlockZ(), pythagorizedLockedLocation2.getBlockZ());

            if(rect1MinX <= rect2MaxX && rect1MinZ <= rect2MaxZ &&
                rect2MinX <= rect1MaxX && rect2MinZ <= rect1MaxZ) {

                System.out.println(key);
                System.out.println("OVERLAP DETECTED!!!");
                if (lockedLand.get(key).isBefore(lockedLand.get(playersInputKey))) {

                    System.out.println("Timing check came back. Player can't buy this land");
                    return true;
                }
            }
        }
        return false;
    }
}
