package net.aesten.wwrpg.config;

import net.aesten.wwrpg.wwrpg;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class MapConfigFile {
    private final File file;
    private FileConfiguration customFile;

    MapConfigFile(String fileName) {
        file = new File(wwrpg.getPlugin().getDataFolder(), fileName + ".yml");

        if (!file.exists()) {
            try{
                file.createNewFile();
            }catch (IOException ignored) {}
        }
        customFile = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration getConfig() {
        return customFile;
    }

    public void saveConfig() {
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
