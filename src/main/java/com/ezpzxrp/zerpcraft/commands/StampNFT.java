package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.database.IDatabaseManager;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static java.time.LocalTime.now;

public class StampNFT implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (sender.isOp()) {

            String playerName = args[0];
            Player player = Bukkit.getPlayer(playerName);
            ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());


            XUMM xumm = ZerpCraft.getXumm();

            if (args[1].equals("clear")) {
                System.out.println("Im in the clear");
                xumm.stampNFT("@xnft:\n" + "zc:\n[]", ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXummToken(), ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress());
                zcPlayer.setNFTCount(0);
                return true;
            }
            String hash = DataHelper.addXLS19EntryToDomain(player,args[1] + ":" + args[2] + ":" + args[3] );
            int nftCount = DataHelper.countNFTsInDomain(hash);
            if(hash.equals("Too Many NFTs")) {

                sender.sendMessage("This player has 5 NFTs already");
                return true;
            }
            if(args[1].equals("FXX")) {

                zcPlayer.setIsFNFTOwner(true);
            }
            zcPlayer.setNFTCount(nftCount);
            zcPlayer.setHasNFT(true);
            zcPlayer.setHasNewNFT(true);
            ZerpCraft.p.registeredPlayers.put(zcPlayer.getUuid(), zcPlayer);
            xumm.stampNFT(hash, ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXummToken(),ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress());

            IDatabaseManager purchaseManager = ZerpCraft.getDatabaseManager();
            purchaseManager.recordPurchase(player.getUniqueId() + " " + args[1] + " " + args[2] + " 0 " + now() + " "  + ZerpCraft.p.registeredPlayers.get(player.getUniqueId()).getProfile().getXrplAddress() + " " + args[1] + ":" + args[2] + ":" + args[3] + " " + player.getName());
        }
        return true;
    }
}