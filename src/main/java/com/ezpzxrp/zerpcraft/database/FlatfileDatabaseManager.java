package com.ezpzxrp.zerpcraft.database;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlatfileDatabaseManager implements IDatabaseManager {

    private final File playerXUMMRegistrationFile;
    private final File auctionHistoryFile;
    private final File foundersNFTStateMap;
    private final File purchaseHistoryFile;
    private final File purchaseCohortsFile;
    private final File xls20MigrationFile;
    private static final Object fileWritingLock = new Object();

    protected FlatfileDatabaseManager() {

        playerXUMMRegistrationFile = new File(ZerpCraft.getPlayerXUMMRegistrationFilePath());
        auctionHistoryFile = new File(ZerpCraft.getAuctionBidHistoryFilePath());
        foundersNFTStateMap = new File(ZerpCraft.getFoundersNFTStateMapFilePath());
        purchaseHistoryFile = new File(ZerpCraft.getPurchaseHistoryFilePath());
        purchaseCohortsFile = new File(ZerpCraft.getPurchaseCohortsFilePath());
        xls20MigrationFile = new File(ZerpCraft.getXLS20MigrationFilePath());
        fileChecks();
    }

    private void fileChecks() {

        if (playerXUMMRegistrationFile.exists()) {

            ZerpCraft.p.getLogger().info("playerXUMMRegistrationFile already exists");

        }
        else {
            playerXUMMRegistrationFile.getParentFile().mkdirs();
            try {

                ZerpCraft.p.getLogger().info("Creating payment preferences file");
                new File(ZerpCraft.getPlayerXUMMRegistrationFilePath()).createNewFile();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        if (purchaseHistoryFile.exists()) {

            ZerpCraft.p.getLogger().info("purchaseHistory already exists");

        }
        else {
            purchaseHistoryFile.getParentFile().mkdirs();
            try {

                ZerpCraft.p.getLogger().info("Creating purchase history file");
                new File(ZerpCraft.getPurchaseHistoryFilePath()).createNewFile();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        if (xls20MigrationFile.exists()) {

            ZerpCraft.p.getLogger().info("xls20MigrationFile already exists");

        }
        else {
            xls20MigrationFile.getParentFile().mkdirs();
            try {

                ZerpCraft.p.getLogger().info("Creating xls20MigrationFile file");
                new File(ZerpCraft.getXLS20MigrationFilePath()).createNewFile();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        if (purchaseCohortsFile.exists()) {

            ZerpCraft.p.getLogger().info("purchaseCohorts already exists");

        }
        else {
            purchaseCohortsFile.getParentFile().mkdirs();
            try {

                ZerpCraft.p.getLogger().info("Creating purchase cohort file");
                new File(ZerpCraft.getPurchaseCohortsFilePath()).createNewFile();
            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
        if (auctionHistoryFile.exists()) {

            ZerpCraft.p.getLogger().info("auctionHistoryFile already exists");
        }
        auctionHistoryFile.getParentFile().mkdirs();
        try {

            ZerpCraft.p.getLogger().info("Creating auction history file");
            new File(ZerpCraft.getAuctionBidHistoryFilePath()).createNewFile();
        }
        catch (IOException e) {

            e.printStackTrace();
        }
        if (foundersNFTStateMap.exists()) {

            ZerpCraft.p.getLogger().info("foundersNFTStateMap already exists");
            return;
        }
        foundersNFTStateMap.getParentFile().mkdirs();
        try {

            ZerpCraft.p.getLogger().info("Creating FNFTState file");
            new File(ZerpCraft.getFoundersNFTStateMapFilePath()).createNewFile();
        }
        catch (IOException e) {

            e.printStackTrace();
        }
    }

    public Map<String, JSONObject> loadXLS20Objects() {

        JSONParser parser = new JSONParser();
        Map<String, JSONObject> nftClaims = new HashMap<String, JSONObject>();

        try {

            JSONArray nftClaimInfo = (JSONArray) parser.parse(new FileReader(ZerpCraft.getXLS20MigrationFilePath()));
            for (Object claimInfo : nftClaimInfo) {

                JSONObject claimInfoJ = (JSONObject) claimInfo;
                //System.out.println("putting object at " + claimInfoJ.get("regionId"));
                nftClaims.put((String) claimInfoJ.get("regionId"), claimInfoJ);
            }
            return nftClaims;
        }
        catch (FileNotFoundException e) {

            e.printStackTrace();
        }
        catch (IOException e) {

            e.printStackTrace();
        }
        catch (org.json.simple.parser.ParseException e) {

            e.printStackTrace();
        }
        return nftClaims;
    }
    
    public Map<UUID, PlayerProfile> loadRegisteredPlayers() {
        
        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getPlayerPaymentPreferencesFilePath();
        Map<UUID, PlayerProfile> registeredPlayers = new HashMap<UUID, PlayerProfile>();
        synchronized (fileWritingLock) {
            try {
                
                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                while ((line = in.readLine()) != null) {
                    
                    String[] character = line.split(":");

                    PlayerProfile player = loadFromLine(character);
                    registeredPlayers.put(player.getUniqueId(), player);
                }
                return registeredPlayers;
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        return null;
    }

    public void loadFNFTStateMap() {

        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getFoundersNFTStateMapFilePath();
        synchronized (fileWritingLock) {
            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                while ((line = in.readLine()) != null) {

                    String[] character = line.split(":");

                    Boolean[] boolArray = new Boolean[2];
                    boolArray[0] = Boolean.parseBoolean(character[1]);
                    boolArray[1] = Boolean.parseBoolean(character[2]);
                    ZerpCraft.p.fNFTMap.put(Integer.parseInt(character[0]), boolArray);
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    public String[] loadAuctionState() {

        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getAuctionBidHistoryFilePath();
        String[] character = null;
        synchronized (fileWritingLock) {
            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                while ((line = in.readLine()) != null) {

                    character = line.split(" ");
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        return character;
    }

    public Map<Integer, String[]> loadPurchaseHistory() {

        Map<Integer, String[]> purchaseHistory = new HashMap<Integer, String[]>();
        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getPurchaseHistoryFilePath();
        String[] character = null;
        synchronized (fileWritingLock) {
            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                int i = 0;
                while ((line = in.readLine()) != null) {

                    i++;
                    character = line.split(" ");
                    purchaseHistory.put(i, character);
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        return purchaseHistory;
    }

    public Map<Integer, String> loadPurchaseCohorts() {

        Map<Integer, String> purchaseHistory = new HashMap<Integer, String>();
        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getPurchaseCohortsFilePath();
        synchronized (fileWritingLock) {
            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                int i = 0;
                while ((line = in.readLine()) != null) {

                    i++;
                    purchaseHistory.put(i, line);
                }
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
        return purchaseHistory;
    }

    public PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean createNew) {

        BufferedReader in = null;
        String usersFilePath = ZerpCraft.getPlayerPaymentPreferencesFilePath();
        synchronized (fileWritingLock) {
            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                String line;
                while ((line = in.readLine()) != null) {

                    // Find the line that contains the player
                    String[] character = line.split(":");

                    //Currently I'm assuming every player has a UUID.
                    // I don't know when that wouldn't be the case, but I may need to learn more
                    if (uuid != null && !character[UUID_INDEX].equalsIgnoreCase(uuid.toString())) {

                        continue;
                    }
                    //We can add in code to detect namechanges here
                    return loadFromLine(character);
                }
                //Didn't find the player
                return new PlayerProfile(playerName, uuid);
            }
            catch (IOException e) {

                e.printStackTrace();
            }
            finally {
                // I have no idea what an inline tryClose() is here, but the comments suggest
                // there will be a resource leak warning, and I'm trusting nossr50 on this one.
                if (in != null) {
                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
            }
        }

        return new PlayerProfile(playerName, uuid);
    }

    public boolean saveUser(PlayerProfile profile) {

        String playerName = profile.getPlayerName();
        UUID uuid = profile.getUniqueId();

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = ZerpCraft.getPlayerPaymentPreferencesFilePath();

        synchronized (fileWritingLock) {

            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;
                boolean wroteUser = false;
                while ((line = in.readLine()) != null) {

                    String[] character = line.split(":");
                    if(!(uuid != null && character[UUID_INDEX].equalsIgnoreCase(uuid.toString())) && !character[USERRNAME].equalsIgnoreCase(playerName)) {

                        writer.append(line).append("\r\n");
                    }
                    else {

                        writeUserToLine(profile, playerName, uuid, writer);
                        wroteUser = true;
                    }
                }

                if(!wroteUser) {

                    writeUserToLine(profile, playerName, uuid, writer);
                }

                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
                return true;
            }
            catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            finally {

                if (in != null) {

                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
                if (out != null) {

                    try {

                        out.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    private PlayerProfile loadFromLine(String[] character) {

        String xrplAddress;
        try {

            xrplAddress = character[XRPL_ADDRESS];
        }
        catch (Exception e) {

            xrplAddress = null;
        }

        String xummToken;
        try {

            xummToken = character[XUMM_TOKEN_INDEX];
        }
        catch (Exception e) {

            xummToken = null;
        }

        UUID uuid;
        try {

            uuid = UUID.fromString(character[UUID_INDEX]);
        }
        catch (Exception e) {

            uuid = null;
        }

        DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {

            date = fmt.parse("2022-04-02 12:00:00");
        }
        catch (ParseException e) {

            date = null;
        }
        int purchaseAmount;
        try {

            purchaseAmount = Integer.parseInt(character[PURCHASE_LIMIT]);
        }
        catch (Exception e) {

            purchaseAmount = -1;
        }
        boolean p2POptIn;
        try {

            p2POptIn = Boolean.parseBoolean(character[P2P_OPT_IN]);;
        }
        catch (Exception e) {

            p2POptIn = false;
        }

        return new PlayerProfile(character[USERRNAME], uuid, xrplAddress, xummToken, date, purchaseAmount, p2POptIn);
    }

    private void writeUserToLine(PlayerProfile profile, String playerName, UUID uuid, StringBuilder writer) {

        writer.append(playerName).append(":");
        writer.append(profile.getXrplAddress()).append(":");
        writer.append(profile.getXummToken()).append(":");
        writer.append(uuid != null ? uuid.toString() : "NULL").append(":");
        writer.append(profile.getPurchaseSize()).append(":");
        writer.append(profile.getP2POptIn()).append(":");
        writer.append("\r\n");
    }

    public boolean recordAuctionBid(String auctionInfo) {

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = ZerpCraft.getAuctionBidHistoryFilePath();

        synchronized (fileWritingLock) {

            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {

                }
                writer.append(auctionInfo);
                writer.append("\r\n");

                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
                return true;
            }
            catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            finally {

                if (in != null) {

                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
                if (out != null) {

                    try {

                        out.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    public boolean recordPurchase(String purchaseInfo) {

        BufferedReader in = null;
        FileWriter out = null;
        String usersFilePath = ZerpCraft.getPurchaseHistoryFilePath();

        synchronized (fileWritingLock) {

            try {

                in = new BufferedReader(new FileReader(usersFilePath));
                StringBuilder writer = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {

                    writer.append(line).append("\r\n");
                }
                writer.append(purchaseInfo);
                writer.append("\r\n");

                out = new FileWriter(usersFilePath);
                out.write(writer.toString());
                return true;
            }
            catch (Exception e) {

                e.printStackTrace();
                return false;
            }
            finally {

                if (in != null) {

                    try {

                        in.close();
                    }
                    catch (IOException e) {}
                }
                if (out != null) {

                    try {

                        out.close();
                    }
                    catch (IOException e) {}
                }
            }
        }
    }

    public static int USERRNAME = 0;
    public static int XRPL_ADDRESS = 1;
    public static int XUMM_TOKEN_INDEX = 2;
    public static int UUID_INDEX = 3;
    public static int PURCHASE_LIMIT = 4;
    public static int P2P_OPT_IN = 5;
}
