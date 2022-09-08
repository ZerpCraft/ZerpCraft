package com.ezpzxrp.zerpcraft.datatypes.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerProfileSaveTask;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PlayerProfile {

    // Can my name be final here? What about name changes?
    private final String playerName;
    private UUID uuid;
    private boolean loaded;
    private String xrplAddress = "";
    private String xummToken;

    private volatile boolean changed;
    private int saveAttempts = 0;
    private boolean purchasePermission = false;
    private Date purchaseDate;
    private int purchaseSize = 0;
    private boolean p2pOptIn = false;
    private ArrayList<String> roles;


    public PlayerProfile(String playerName, UUID uuid) {

        this.playerName = playerName;
        this.uuid = uuid;
        this.changed = false;
        DateFormat fmt = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        Date date;
        try {

            date = fmt.parse("2022-04-02 12:00:00");
        }
        catch (ParseException e) {

            date = null;
        }
        this.purchaseDate = date;
        Date currentDate = new Date(System.currentTimeMillis());
        if (currentDate.after(purchaseDate) || currentDate.equals(purchaseDate)) {

            this.purchasePermission = true;
        }
        loaded = true;
    }

    public PlayerProfile(String playerName, UUID uuid, String xrplAddress, String xummToken, Date purchaseDate, int purchaseSize, boolean p2POptIn) {

        this.playerName = playerName;
        this.uuid = uuid;
        this.changed = false;
        this.xrplAddress = xrplAddress;
        this.xummToken = xummToken;
        this.p2pOptIn = p2POptIn;
        Date currentDate = new Date(System.currentTimeMillis());

        if (currentDate.after(purchaseDate) || currentDate.equals(purchaseDate)) {

            this.purchasePermission = true;
        }
        this.purchaseDate = purchaseDate;
        this.purchaseSize = 5000;

        loaded = true;
    }




    public String getPlayerName() {
        return playerName;
    }
    public UUID getUniqueId() {
        return uuid;
    }

    public String getXrplAddress() { return xrplAddress; }
    public void setXrplAddress(String xrplAddress) { this.xrplAddress = xrplAddress; }

    public String getXummToken() { return xummToken; }
    public void setXummToken(String XummToken) { this.xummToken = XummToken; }

    public Boolean getChanged() { return changed; }
    public void setChanged() { this.changed = true; }

    public boolean getPurchasePermission() { return purchasePermission; }
    public void setPurchasePermission(Boolean purchasePermission)  { this.purchasePermission = purchasePermission; }

    public Date getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(Date purchaseDate) { this.purchaseDate = purchaseDate; }

    public int getPurchaseSize() { return purchaseSize; }
    public void setPurchaseSize( int purchaseSize ) {this.purchaseSize = purchaseSize;}

    public boolean getP2POptIn() { return p2pOptIn; }
    public void setP2POptIn(Boolean p2pOptIn) { this.p2pOptIn = p2pOptIn; }

    public ArrayList<String> getRoles() { return roles; }
    public void setRoles(ArrayList<String> roles) { this.roles = roles; }
    public void addRole(String roleName) {
        roles.add(roleName);
        setupRoles(roles);
    }
    public void setupRoles(ArrayList<String> roles) {

        net.luckperms.api.model.user.UserManager userManager = ZerpCraft.p.api.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(uuid);
        // Remove roles that no longer belong
        for(String oldRole : this.roles) {

            Boolean keepRole = false;
            for(String newRole : roles  ) {

               if(newRole.equals(oldRole)) {

                   keepRole = true;
               }
            }
            if(!keepRole) {

                userFuture.thenAcceptAsync((user -> {
                    InheritanceNode node = InheritanceNode.builder(oldRole).value(true).build();
                    DataMutateResult result = user.data().remove(node);
                    userManager.saveUser(user);
                }));
            }
        }
        setRoles(roles);
        // Add new roles to LuckPerms
        for (String role : roles) {

            userFuture.thenAcceptAsync((user -> {
                InheritanceNode node = InheritanceNode.builder(role).value(true).build();
                DataMutateResult result = user.data().add(node);
                userManager.saveUser(user);
            }));
        }
    }
}
