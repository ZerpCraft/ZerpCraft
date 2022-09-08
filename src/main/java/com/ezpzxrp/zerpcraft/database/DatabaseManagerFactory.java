package com.ezpzxrp.zerpcraft.database;

import com.ezpzxrp.zerpcraft.ZerpCraft;

public class DatabaseManagerFactory {

    public static IDatabaseManager getDatabaseManager() {

        ZerpCraft.p.getLogger().info("Setting up flatfile DB");
        return new FlatfileDatabaseManager();
    }
}
