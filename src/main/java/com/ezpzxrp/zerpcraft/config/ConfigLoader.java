package com.ezpzxrp.zerpcraft.config;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class ConfigLoader {

    protected static final ZerpCraft plugin = ZerpCraft.p;
    protected String fileName;
    protected final File configFile;
    protected FileConfiguration config;

    public ConfigLoader(String fileName) {

        this.fileName = fileName;
        configFile = new File(plugin.getDataFolder(), fileName);
        loadFile();
    }

    protected void loadFile() {

        config = YamlConfiguration.loadConfiguration(configFile);
    }
}
