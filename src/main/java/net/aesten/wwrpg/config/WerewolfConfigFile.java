package net.aesten.wwrpg.config;

import net.aesten.wwrpg.wwrpg;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class WerewolfConfigFile {
    private FileConfiguration configFile;

    WerewolfConfigFile() {
        File file = new File(wwrpg.getPlugin().getDataFolder(), "config.yml");

        if (!file.exists()) {
            try{
                InputStream defaultConfigContent = getClass().getClassLoader().getResourceAsStream("config-default.yml");
                assert defaultConfigContent != null;
                Files.copy(defaultConfigContent, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                configFile = YamlConfiguration.loadConfiguration(file);
            }catch (IOException ignored) {}
        }
        else {
            configFile = wwrpg.getPlugin().getConfig();
        }
    }

    public FileConfiguration getConfig() {
        return configFile;
    }

    public void saveConfig() {
        wwrpg.getPlugin().saveConfig();
    }

    public void reloadConfig() {
        wwrpg.getPlugin().reloadConfig();
    }
}
