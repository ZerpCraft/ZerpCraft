package com.ezpzxrp.zerpcraft.XRPL;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XLS20Devnet;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class XRPLService {

    public static JSONArray getZerpCraftNFTsFromWallet(ZerpCraftPlayer zcPlayer) {

        XLS20Devnet requestManager = new XLS20Devnet();
        JSONObject response = requestManager.getWalletNFTInfo(zcPlayer.getProfile().getXrplAddress(), null);
        JSONArray zerpCraftNFTs = new JSONArray();

        zerpCraftNFTs = pullNFTsFromAccountNFTsObject(response);
        while (ResponseParser.getValueFromJsonKeypath(response,"results.marker") != null) {

            response = requestManager.getWalletNFTInfo(zcPlayer.getProfile().getXrplAddress(), ResponseParser.getValueFromJsonKeypath(response,"results.marker"));
            zerpCraftNFTs.addAll(pullNFTsFromAccountNFTsObject(response));
        }

        return zerpCraftNFTs;
    }

    public static JSONArray pullNFTsFromAccountNFTsObject(JSONObject response) {

        JSONArray zerpCraftNFTs = new JSONArray();
        JSONObject result = (JSONObject) response.get("result");
        for(Object nft : (JSONArray) result.get("account_nfts")) {

            JSONObject nftObject = (JSONObject) nft;
            if(nftObject.get("Issuer").equals("razjF3YgtuG7VZvDLNz4bsaHuHksmp6igG")) {

                zerpCraftNFTs.add(nftObject);
            }
        }
        return zerpCraftNFTs;
    }
}
