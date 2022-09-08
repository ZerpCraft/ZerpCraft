package com.ezpzxrp.zerpcraft.config;

import java.util.List;
import java.util.Map;

public class Config extends AutoUpdateConfigLoader {

    private static Config instance;

    private Config() { super("config.yml"); }

    public static Config getInstance() {

        if (instance== null) {

            instance = new Config();
        }

        return instance;
    }

    public String getXUMMApiKey() { return config.getString("General.Server.XUMM.API_KEY");};
    public String getXUMMApiSecret() { return config.getString("General.Server.XUMM.API_SECRET");}
    public String getWalletAddress() { return config.getString("General.Server.Wallet");}
    public List<Map<?,?>> getRoles() { return config.getMapList("General.NFT.Roles");}
}
