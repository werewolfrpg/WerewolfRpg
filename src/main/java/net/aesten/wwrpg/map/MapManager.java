package net.aesten.wwrpg.map;

import net.aesten.wwrpg.WerewolfRpg;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.azalealibrary.configuration.FileConfiguration;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class MapManager {
    private final WorldManager worldManager;
    private final MapEditingHelper helper = new MapEditingHelper();
    private final Map<String, WerewolfMap> maps = new HashMap<>();

    public MapManager(WorldManager worldManager) {
        this.worldManager = worldManager;
        loadMaps();
    }

    public MapEditingHelper getHelper() {
        return helper;
    }

    private void loadMaps() {
        for (FileConfiguration fileConfiguration : AzaleaConfigurationApi.getAllFileConfigurations(WerewolfRpg.getPlugin(), "/wwrpg-maps")) {
            WerewolfMap mapConfig = new WerewolfMap();
            fileConfiguration.load(mapConfig);
            AzaleaConfigurationApi.register(mapConfig);
            maps.put(mapConfig.getMapName(), mapConfig);
        }
    }

    public void unloadMaps() {
        for (WerewolfMap map : maps.values()) {
            AzaleaConfigurationApi.save(WerewolfRpg.getPlugin(), map);
        }
    }

    public boolean createMap(String mapName, World world) {
        if (maps.get(mapName) != null) return false;
        WerewolfMap newMap = new WerewolfMap(mapName, world);
        maps.put(mapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), mapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

//    public boolean copyMap(String mapName, String newMapName) {
//        if (maps.get(mapName) == null) return false;
//        if (maps.get(newMapName) != null) return false;
//        WerewolfMap newMap = new WerewolfMap(maps.get(mapName));
//        maps.put(newMapName, newMap);
//        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), newMapName).load(newMap);
//        AzaleaConfigurationApi.register(newMap);
//        return true;
//    }

//    public boolean renameMapConfigFile(String mapName, String newMapName) {
//        return copyMap(mapName, newMapName) && deleteMap(mapName);
//    }

    public boolean deleteMap(WerewolfMap map) {
        AzaleaConfigurationApi.unregister(map);
        return maps.remove(map.getMapName()) != null;
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
