package com.ezpzxrp.zerpcraft.XUMM;

import com.ezpzxrp.zerpcraft.XRPL.XRPLService;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerAssignLandInWalletTask;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerCheckDomainContentsTask;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerGamemodeCreativeTask;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;

import java.util.concurrent.CompletableFuture;

public class RegistrationTask extends BukkitRunnable {

    //private final String address;
    private final Player player;

    public RegistrationTask(Player player) {

        this.player = player;
    }

    @Override
    public void run() {

        ZerpCraft.p.getLogger().info("Running registration task");
        try {


            XUMMController.handleRegistration(player);
            player.sendMessage("Registration complete! Checking your wallet for ZerpCraft NFTs...");
            ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
            JSONArray zerpCraftNFTs = XRPLService.getZerpCraftNFTsFromWallet(zcPlayer);
            new PlayerCheckDomainContentsTask(zcPlayer, zerpCraftNFTs);
            new PlayerAssignLandInWalletTask(zcPlayer, zerpCraftNFTs);
        }
        catch (XUMMController | XUMM e) {

            if(e.getMessage().equals("Expired registration link")) {

                player.sendMessage("Registration timeout. Please use /zcregister to try again");
            }
            else if (e.getMessage().equals("Duplicate Registration")) {

                player.sendMessage("This wallet is already registered to another player.");
            }
            else {
                player.sendMessage("Registration error. Please log off and on again, that should fix your issue.");
            }
            e.printStackTrace();
        }
    }
}
