package net.aesten.wwrpg.map;

import net.aesten.wwrpg.WerewolfRpg;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import org.bukkit.World;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MapManager {
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
        String dataDir = WerewolfRpg.getPlugin().getDataFolder().getPath();
        File mapsDir = new File(dataDir + File.separator + "wwrpg-maps");

        if (!mapsDir.mkdir()) {
            File[] mapsArray = mapsDir.listFiles();
            if (mapsArray != null) {
                for (File map : mapsArray) {
                    String mapName = "wwrpg-maps" + File.separator + map.getName().split("\\.")[0];
                    WerewolfMap mapConfig = new WerewolfMap();
                    AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), mapName).load(mapConfig);
                    AzaleaConfigurationApi.register(mapConfig);
                    maps.put(mapName, mapConfig);
                }
            }
        }
    }

    private void loadLobby() {
        if (maps.get("wwrpg-maps" + File.separator + "lobby") == null) {
            if (createMap("lobby", worldManager.getLobby())) {
                WerewolfRpg.logConsole("Auto-generated lobby map");
            }
        }
    }

    private void duplicateSave() {
        WerewolfRpg.getMapsDuplicate().addAll(maps.values());
    }

    public void saveMaps() {
        for (WerewolfMap map : maps.values()) {
            AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), map.getName()).save(map);
        }
    }

    public boolean createMap(String name, World world) {
        String mapName = "wwrpg-maps" + File.separator + name;
        if (maps.get(mapName) != null) return false;
        WerewolfMap newMap = new WerewolfMap(mapName, world);
        maps.put(mapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), mapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        WerewolfRpg.getMapsDuplicate().add(newMap);
        return true;
    }

    public boolean copyMap(WerewolfMap map, String newMapName) {
        if (map == null) return false;
        if (maps.get(newMapName) != null) return false;
        WerewolfMap newMap = new WerewolfMap(map);
        maps.put(newMapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), newMapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

    public boolean renameMapConfigFile(WerewolfMap map, String newMapName) {
        return copyMap(map, newMapName) && deleteMap(map);
    }

    public boolean deleteMap(WerewolfMap map) {
        AzaleaConfigurationApi.unregister(map);
        WerewolfRpg.getMapsDuplicate().remove(map);
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
