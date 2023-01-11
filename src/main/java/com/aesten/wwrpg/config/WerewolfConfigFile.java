package com.aesten.wwrpg.config;

import com.aesten.wwrpg.wwrpg;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class WerewolfConfigFile {
    private final File file;
    private FileConfiguration customFile;

    WerewolfConfigFile(String fileName) {
        file = new File(wwrpg.getPlugin().getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try{
                file.createNewFile();
            }catch (IOException ignored) {}
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getCustomConfig() {
        return customFile;
    }

    public static FileConfiguration getConfig() {
        return wwrpg.getPlugin().getConfig();
    }

    public static void saveConfig() {
        wwrpg.getPlugin().saveConfig();
    }

    public static void reloadConfig() {
        wwrpg.getPlugin().reloadConfig();
    }

    public void save() {
        try{
            customFile.save(file);
        }catch (IOException e) {
            System.out.println("File could not be saved");
        }
    }

    public void reload() {
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public static void init() {
        if (!wwrpg.getPlugin().getDataFolder().exists()) {
            wwrpg.getPlugin().getDataFolder().mkdirs();
        }
    }
}
