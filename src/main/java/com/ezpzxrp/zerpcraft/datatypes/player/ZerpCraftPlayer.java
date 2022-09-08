package com.ezpzxrp.zerpcraft.datatypes.player;

import com.ezpzxrp.zerpcraft.XUMM.util.PaymentUtils;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XRPLCluster;
import com.ezpzxrp.zerpcraft.datatypes.nft.Plot;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerProfileSaveTask;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.xml.xpath.XPath;
import java.text.ParseException;
import java.util.*;

public class ZerpCraftPlayer {

    private  Player player;
    private PlayerProfile profile;
    private final String playerName;
    private final UUID uuid;
    private Map<String, Plot> playerPlots = new HashMap<>();
    private Location pos1;
    private Location pos2;
    private boolean isFNFTOwner;
    private boolean hasNFT;
    private boolean hasNewNFT;
    private boolean isPurchasing = false;
    private int NFTCount;


    private int saveAttempts = 0;

    public ZerpCraftPlayer(Player player, PlayerProfile profile) {

        this.playerName = player.getName();
        this.uuid = player.getUniqueId();
        this.player = player;
        this.profile = profile;
        this.isFNFTOwner = false;
        this.hasNFT = false;
        this.hasNewNFT = false;

    }

    public ZerpCraftPlayer(PlayerProfile profile) {

        this.uuid = profile.getUniqueId();
        this.profile = profile;
        this.player = Bukkit.getPlayer(profile.getUniqueId());
        this.playerName = profile.getPlayerName();
        this.isFNFTOwner = false;
        this.hasNFT = false;
        this.hasNewNFT = false;
    }

    public void logout(boolean syncSave) {

        Player thisPlayer = getPlayer();

        if (syncSave) {
            System.out.println("Saving");
            save(true);
        } else {
            scheduleAsyncSave();

            System.out.println("Not Saving");
            // TODO: I think that server saving goes here
        }

        UserManager.remove(thisPlayer);
    }

    public void save(Boolean useSync) {

        System.out.println(getProfile().getChanged());
        System.out.println(getProfile().getXrplAddress());

        if (getProfile().getChanged()) {

            System.out.println("Profile has changed, saving");
            Boolean saveSuccess = ZerpCraft.getDatabaseManager().saveUser(getProfile());
            if (!saveSuccess) {

                ZerpCraft.p.getLogger().severe("PlayerProfile saving failed for player: " + playerName + " " + uuid);
                if (saveAttempts > 0) {

                    ZerpCraft.p.getLogger().severe("Attempted to save profile for player " + getPlayerName() +
                            " resulted in failure. " + saveAttempts + " have been made so far.");
                }
                if (saveAttempts < 10) {

                    saveAttempts++;
                    if (ZerpCraft.isServerShutdownExecuted() || useSync) {

                        new PlayerProfileSaveTask(this, true).runTask(ZerpCraft.p);
                    } else {

                        scheduleAsyncSave();
                    }
                    //TODO: Some server shutdown code needs to live here
                } else {
                    ZerpCraft.p.getLogger().severe("XRPTipper has failed to save the profile for "
                            + getPlayerName() + " numerous times." +
                            " XRPTipper will now stop attempting to save this profile." +
                            " Check your console for errors and inspect your DB for issues.");
                }
            } else {

                saveAttempts = 0;
            }
        }
    }

    public void scheduleAsyncSave() {

        new PlayerProfileSaveTask(this, false).runTaskAsynchronously(ZerpCraft.p);
    }

    public Player getPlayer() {

        return player;
    }

    public void setPlayer(Player player) {

        this.player = player;
    }

    public String getPlayerName() {

        return playerName;
    }

    public UUID getUuid() {

        return uuid;
    }

    public PlayerProfile getProfile() {

        if (profile == null) {

            return new PlayerProfile(getPlayerName(), player.getUniqueId());
        }
        return profile;
    }

    public void setProfile(PlayerProfile profile) {

        this.profile = profile;
    }

    public Plot getPlot(String hash) {

        return safe(this.playerPlots).get(hash);
    }

    public Location getPos1() {

        return pos1;
    }

    public void setPos1( Location pos1 ) {

        this.pos1 = pos1;
    }

    public Location getPos2() {

        return pos2;
    }

    public void setPos2( Location pos2 ) {

        this.pos2 = pos2;
    }

    public boolean getisFNFTOwner() {

        return isFNFTOwner;
    }

    public void setIsFNFTOwner( boolean owner ) {

        this.isFNFTOwner = owner;
    }

    public boolean getHasNFT() {

        return hasNFT;
    }

    public void setHasNewNFT( boolean status ) {

        this.hasNewNFT = status;
    }

    public boolean getHasNewNFT() {

        return hasNewNFT;
    }

    public void setNFTCount( int NFTCount ) {

        this.NFTCount = NFTCount;
    }

    public int getNFTCount() {

        return NFTCount;
    }

    public void setIsPurchasing( boolean status ) {

        this.isPurchasing = status;
    }

    public boolean getIsPurchasing() {

        return isPurchasing;
    }

    public void setHasNFT( boolean owner ) {

        this.hasNFT = owner;
    }

    public Map<String, Plot> getPlots() {

        return playerPlots;
    }

    public void addPlot(String plotHash, Plot plot) {

        this.playerPlots.put(plotHash,plot);
    }

    public void clearPlayerPlots() {

        List<String> playerPlots = new ArrayList<>(this.playerPlots.keySet());

        for (String hash : playerPlots) {

            System.out.println("Do I get into the loop at all?");
            System.out.println(hash);
            Plot plot = this.playerPlots.get(hash);
            removePlot(plot);
        }
    }
    public void unlockPlots() {

        Map<String, Plot> playerPlots = safe( this.playerPlots );
        for (Map.Entry<String, Plot> entry : playerPlots.entrySet()) {

            Plot plot = entry.getValue();
            plot.unlock();
        }
    }

    public boolean removePlot(Plot plot) {

        if (plot.getIsLocked()) {
            System.out.println("this plot is locked");
            return false;
        }
        System.out.println("deconstructing realm");
        plot.deconstructRealm(plot.getRegionId());
        this.playerPlots.remove(plot.getHash());
        return true;
    }

    public static Map<String, Plot> safe( Map<String, Plot> other ) {
        return other == null ? Collections.EMPTY_MAP : other;
    }

    public Map<String, XLS20> getNFTs() {

        String xrplAddress = profile.getXrplAddress();
        String marker = "";
        XRPLCluster publicNode = new XRPLCluster();
        Map<String, XLS20> zerpCraftNFTs = null;


        while (marker != null) {

            JSONObject response = publicNode.getWalletNFTInfo(xrplAddress, marker);
            String account_nfts = ResponseParser.getValueFromJsonKeypath(response,"result.account_nfts");
            JSONParser parser = new JSONParser();
            try {

                JSONArray nftArray = (JSONArray) parser.parse(account_nfts);
                for (int i = 0; i < nftArray.size(); i++) {

                    JSONObject obj = (JSONObject) nftArray.get(i);
                    String issuer = (String) obj.get("issuer");
                    if(issuer.equals("razjF3YgtuG7VZvDLNz4bsaHuHksmp6igG")) {
                        XLS20 nft = new XLS20((String) obj.get("NFTokenId"), (String) obj.get("URI"));
                        zerpCraftNFTs.put(nft.getTokenId(), nft);
                    }
                }
                marker = ResponseParser.getValueFromJsonKeypath(response,"result.marker");
            }
            catch ( org.json.simple.parser.ParseException e) {}
        }

        return zerpCraftNFTs;
    }
}