package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerGamemodeCreativeTask extends BukkitRunnable {

    private final Player player;

    public PlayerGamemodeCreativeTask(Player player) {

        this.player = player;
    }

    @Override
    public void run() {

        //Quit if the player is logged out
        if (!player.isOnline()) {

            ZerpCraft.p.getLogger().info("Aborting profile loading for " + player.getName());
            return;
        }
        player.setGameMode(GameMode.CREATIVE);
    }
}
