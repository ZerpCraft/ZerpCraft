package com.ezpzxrp.zerpcraft.util.player;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.S1;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS19;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import java.util.UUID;

public class CheckHelper extends Throwable {

    //I may move these into functions on the zcPlayer at a later date
    public static boolean checkIsRegistered(Player player) {

        UUID id = player.getUniqueId();
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(id);
        System.out.println(zcPlayer.getProfile());
        return zcPlayer.getProfile() != null;
    }

    public static boolean checkHasSufficientXRP(Player player, int amountToVerify) {

        double currentPlayerXRPBalance = DataHelper.getWalletBalanceByPlayerMinusReserves(player);
        if (currentPlayerXRPBalance < amountToVerify) {

            return false;
        }
        return true;
    }

    public static boolean isFNFTOwner(Player player) throws CheckHelper {

        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        S1 publicNode = new S1();
        JSONObject responseObject = publicNode.getWalletAccountInfo(zcPlayer.getProfile().getXrplAddress());
        String nftType = "";
        String xls19NftDomain;
        xls19NftDomain = ResponseParser.decodeDomain(responseObject);
        System.out.println(xls19NftDomain);
        if(xls19NftDomain.equals("")) {

            return false;
        }
        try {
            String theString = xls19NftDomain.substring(xls19NftDomain.indexOf('['), xls19NftDomain.indexOf(']'));
            String[] splittedString = theString.substring(1, theString.length()-1).split(",");
            int nftCount = 0;
            for (String hash : splittedString) {

                nftCount++;
                if (nftCount >= 5) {

                    throw new CheckHelper("Too Many NFTs");
                }
                try {

                    System.out.println(hash);
                    XLS19 nft = new XLS19(hash);
                    nftType = nft.getPrefixFlag();
                    System.out.println(nftType);
                    if (nftType.equals("FXX") ) {

                        return true;
                    }
                }
                catch (Exception e) {

                    e.printStackTrace();
                    throw new CheckHelper("");
                }
            }
        }
        catch (Exception e) {

            if (e.getMessage().equals("Too Many NFTs")) {

                throw new CheckHelper("Too Many NFTs");
            }
            else {

                throw new CheckHelper("");
            }
        }

        return false;
    }

    public static boolean isLandLocked(int nftNumber) {

        return ZerpCraft.p.fNFTMap.get(nftNumber)[0];
    }

    public static boolean isLandPurchased(int nftNumber) {

        return ZerpCraft.p.fNFTMap.get(nftNumber)[1];
    }

    public CheckHelper(String errorMessage) {

        super(errorMessage);
    }
}
