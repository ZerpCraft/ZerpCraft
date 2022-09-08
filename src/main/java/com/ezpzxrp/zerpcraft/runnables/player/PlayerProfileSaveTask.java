package com.ezpzxrp.zerpcraft.runnables.player;

import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerProfileSaveTask extends BukkitRunnable {

    private final ZerpCraftPlayer zcPlayer;
    private final boolean isSync;

    public PlayerProfileSaveTask(ZerpCraftPlayer zcPlayer, boolean isSync) {

        this.zcPlayer = zcPlayer;
        this.isSync = isSync;
    }

    @Override
    public void run() {

        zcPlayer.save(isSync);
    }
}
