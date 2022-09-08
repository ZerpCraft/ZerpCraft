package com.ezpzxrp.zerpcraft.datatypes.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;

import java.util.Map;
import java.util.UUID;

public class PlayerService {

    public static ZerpCraftPlayer getPlayerByWallet(String walletAddress) {

        Map<UUID,ZerpCraftPlayer> zcPlayers = ZerpCraft.p.registeredPlayers;
        for (UUID uuid : zcPlayers.keySet()) {

            ZerpCraftPlayer zcPlayer = zcPlayers.get(uuid);
            String playerWalletAddress = zcPlayer.getProfile().getXrplAddress();
            if(walletAddress.equals(playerWalletAddress)) {
                return zcPlayer;
            }
        }
        return null;
    }
}
