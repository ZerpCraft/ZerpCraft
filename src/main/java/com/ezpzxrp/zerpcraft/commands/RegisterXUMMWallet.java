package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.XUMM.RegistrationTask;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RegisterXUMMWallet implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        ZerpCraft.p.getLogger().info("Executing zcRegister command");

        ZerpCraft.p.getLogger().info("Setup Player");
        Player player = (Player) sender;
        ZerpCraftPlayer zcPlayer = UserManager.getPlayer(player);

        ZerpCraft.p.getLogger().info("Getting player profile");
        PlayerProfile profile = zcPlayer.getProfile();
        if (profile == null) {

            ZerpCraft.p.getLogger().info("No profile found. Creating a new profile");
            zcPlayer.setProfile(new PlayerProfile(zcPlayer.getPlayerName(), zcPlayer.getUuid()));
        }

        ZerpCraft.p.getLogger().info("Initiate XUMM registration task");
        new RegistrationTask(player).runTaskLaterAsynchronously(ZerpCraft.p, 60);

        return true;
    }

}
