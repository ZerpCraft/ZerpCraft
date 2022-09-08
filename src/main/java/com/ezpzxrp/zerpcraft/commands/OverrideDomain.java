package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;


public class OverrideDomain implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.isOp()) {

            return true;
        }

        UUID playerUUID = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
        ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(playerUUID);

        String hash = args[1];
        hash = "@xnft:\n" + "zc:\n[" + hash + "]";

        int nftCount = DataHelper.countNFTsInDomain(hash);
        zcPlayer.setNFTCount(nftCount);
        zcPlayer.setHasNFT(true);
        zcPlayer.setHasNewNFT(true);
        XUMM xumm = ZerpCraft.getXumm();
        xumm.stampNFT(hash, zcPlayer.getProfile().getXummToken(), zcPlayer.getProfile().getXrplAddress());

        return true;
    }
}
