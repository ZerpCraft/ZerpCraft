package com.ezpzxrp.zerpcraft.XRPL;

import com.ezpzxrp.zerpcraft.XUMM.util.PaymentUtils;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.S1;
import com.ezpzxrp.zerpcraft.callouts.S2;
import com.ezpzxrp.zerpcraft.callouts.XLS20Devnet;
import com.ezpzxrp.zerpcraft.callouts.XRPLCluster;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

public class DataHelper extends Throwable {

    public static double getWalletBalanceByPlayerMinusReserves(Player player) {

        String xrplAddress = UserManager.getPlayer(player).getProfile().getXrplAddress();
        S2 s1Request = new S2();
        JSONObject response = s1Request.getWalletAccountInfo(xrplAddress);
        String balanceFromXRPL = ResponseParser.getValueFromJsonKeypath(response, "result.account_data.Balance");
        double balance = PaymentUtils.convertDropsToDouble(balanceFromXRPL);
        long reserves = (ResponseParser.getNumberFromJsonKeypath(response, "result.account_data.OwnerCount") * 2) + 10;

        return balance - reserves;
    }

    public static String addXLS19EntryToDomain(Player player, String hash) {


        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        XRPLCluster publicNode = new XRPLCluster();
        JSONObject responseObject = publicNode.getWalletAccountInfo(zcPlayer.getProfile().getXrplAddress());
        String xls19NftDomain;
        xls19NftDomain = ResponseParser.decodeDomain(responseObject);
        if(xls19NftDomain.equals("")) {

            return "@xnft:\n" + "zc:\n[" + hash + "]";
        }
        StringBuilder returnHash = new StringBuilder("@xnft:\n" + "zc:\n[" + hash);
        try {

            if (!xls19NftDomain.equals("@xnft:\n" + "zc:\n[]")) {

                String theString = xls19NftDomain.substring(xls19NftDomain.indexOf('[') + 1, xls19NftDomain.indexOf(']'));
                String[] splittedString = theString.substring(1, theString.length()-1).split(",");
                int nftCount = 0;
                for (String oldHash : splittedString) {

                    nftCount++;
                    if(nftCount >= 5) {

                        throw new DataHelper("Too Many NFTs");
                    }
                }
                returnHash.append(",").append(theString);
                System.out.println(returnHash);
            }
            returnHash.append("]");
        }
        catch (Exception | DataHelper e) {

            if(e.getMessage().equals("Too Many NFTs")) {
                return "Too Many NFTs";
            }
            e.printStackTrace();
            return "@xnft:\n" + "zc:\n[" + hash + "]";
        }

        return returnHash.toString();
    }

    public static int countNFTsInDomain(String domain) {

        if(domain.equals("")) {

            return 0;
        }
        if (!domain.equals("@xnft:\n" + "zc:\n[]")) {

            try {

                String theString = domain.substring(domain.indexOf('[') + 1, domain.indexOf(']'));



                String[] splittedString = theString.substring(1, theString.length()-1).split(",");
                int nftCount = 0;
                for (String oldHash : splittedString) {

                    nftCount++;
                }
                return nftCount;
            }
            catch(Exception e) {

                return 0;
            }
        }
        else {

            return 0;
        }
    }

    public static JSONObject convertNFTToTransferObject(JSONObject nft, String walletId) {

        JSONObject transferEvent = new JSONObject();
        transferEvent.put("URI",nft.get("URI"));
        transferEvent.put("NFTId",nft.get("NFTokenID"));
        transferEvent.put("DestinationWallet",nft.get(walletId));


        return transferEvent;
    }

    public DataHelper(String errorMessage) {

        super(errorMessage);
    }
}
