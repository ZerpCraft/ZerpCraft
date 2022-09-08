package com.ezpzxrp.zerpcraft.database;

import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.UUID;

public interface IDatabaseManager {

    PlayerProfile loadPlayerProfile(String playerName, UUID uuid, boolean createNew);
    Map<UUID, PlayerProfile> loadRegisteredPlayers();
    void loadFNFTStateMap();
    boolean saveUser(PlayerProfile profile);
    boolean recordAuctionBid(String bidInfo);
    boolean recordPurchase(String purchaseInfo);
    String[] loadAuctionState();
    Map<Integer, String[]> loadPurchaseHistory();
    Map<Integer, String> loadPurchaseCohorts();
    Map<String, JSONObject> loadXLS20Objects();
}
