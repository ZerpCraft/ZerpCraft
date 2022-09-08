package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XRPL.XRPLService;
import com.ezpzxrp.zerpcraft.XUMM.util.PaymentUtils;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XLS20Devnet;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.ezpzxrp.zerpcraft.util.player.CheckHelper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class PlayerCheckDomainContentsTask extends BukkitRunnable {

    private ZerpCraftPlayer zcPlayer;
    JSONArray zerpCraftNFTs;

    public PlayerCheckDomainContentsTask(ZerpCraftPlayer zcPlayer, JSONArray zerpCraftNFTs) {

        this.zcPlayer = zcPlayer;
        this.zerpCraftNFTs = zerpCraftNFTs;
    }

    @Override
    public void run() {


        ArrayList<String> roles = determinePlayerRoles(zerpCraftNFTs);
        zcPlayer.getProfile().setupRoles(roles);
    }

    public static ArrayList<String> determinePlayerRoles(JSONArray zerpCraftNFTs) {

        ArrayList<String> roles = new ArrayList<String>();
        for(Object nft : zerpCraftNFTs) {

            JSONObject nftObject = (JSONObject) nft;
            String URI = (String) nftObject.get("URI");
            String tokenId = (String) nftObject.get("NFTokenID");
            XLS20 xls20 = new XLS20(tokenId, URI);

            String prefix = xls20.getPrefixFlag();
            String role = (String) ZerpCraft.p.roles.get(prefix).get("RoleName");
            roles.add(role);
        }

        return roles;
    }
}
