package net.aesten.werewolfmc.plugin.map;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.azalealibrary.configuration.FileConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapManager {
    private static final String LOBBY = "lobby";
    private final WorldManager worldManager;
    private final MapEditingHelper helper = new MapEditingHelper();
    private final Map<String, WerewolfMap> maps = new HashMap<>();

    public MapManager(WorldManager worldManager) {
        this.worldManager = worldManager;
        loadMaps();
        loadLobby();
    }

    public MapEditingHelper getHelper() {
        return helper;
    }

    private void loadMaps() {
        for (FileConfiguration fileConfiguration : AzaleaConfigurationApi.getAllFileConfigurations(WerewolfPlugin.getPlugin(), "/werewolf-maps")) {
            WerewolfMap mapConfig = new WerewolfMap(fileConfiguration.getName(), Objects.requireNonNull(Bukkit.getWorld(LOBBY)));
            fileConfiguration.load(mapConfig);
            AzaleaConfigurationApi.register(mapConfig);
            maps.put(mapConfig.getName(), mapConfig);
        }
    }

    private void loadLobby() {
        if (maps.get(LOBBY) == null) {
            if (createMap(LOBBY, worldManager.getLobby())) {
                WerewolfPlugin.logConsole("Auto-generated lobby map");
            }
        }
    }

    public void saveMaps() {
        for (WerewolfMap map : maps.values()) {
            AzaleaConfigurationApi.getFileConfiguration(WerewolfPlugin.getPlugin(),  "werewolf-maps" + File.separator + map.getName()).save(map);
        }
    }

    public boolean createMap(String name, World world) {
        if (maps.get(name) != null) return false;
        WerewolfMap newMap = new WerewolfMap(name, world);
        maps.put(name, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfPlugin.getPlugin(), name).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

    public boolean copyMap(WerewolfMap map, String newMapName) {
        if (map == null) return false;
        if (maps.get(newMapName) != null) return false;
        WerewolfMap newMap = new WerewolfMap(map);
        maps.put(newMapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfPlugin.getPlugin(), newMapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

    public boolean renameMapConfigFile(WerewolfMap map, String newMapName) {
        return copyMap(map, newMapName) && deleteMap(map);
    }

    public boolean deleteMap(WerewolfMap map) {
        AzaleaConfigurationApi.unregister(map);
        return maps.remove(map.getName()) != null;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public Map<String, WerewolfMap> getMaps() {
        return maps;
    }

    public WerewolfMap getMapFromName(String name) {
        return maps.get(name);
    }
}
