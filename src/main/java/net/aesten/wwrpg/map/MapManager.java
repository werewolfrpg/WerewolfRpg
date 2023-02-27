package net.aesten.wwrpg.map;

import net.aesten.wwrpg.WerewolfRpg;
import net.azalealibrary.configuration.AzaleaConfigurationApi;

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
                    maps.put(mapConfig.getMapName(), mapConfig);
                }
            }
        }
    }

    public boolean createMap(String mapName, String worldName) {
        if (maps.get(mapName) != null) return false;
        WerewolfMap newMap = new WerewolfMap(mapName, worldManager.getWorldFromName(worldName));
        maps.put(mapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), mapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

    public boolean copyMap(String mapName, String newMapName) {
        if (maps.get(mapName) == null) return false;
        if (maps.get(newMapName) != null) return false;
        WerewolfMap newMap =new WerewolfMap(maps.get(mapName));
        maps.put(newMapName, newMap);
        AzaleaConfigurationApi.getFileConfiguration(WerewolfRpg.getPlugin(), newMapName).load(newMap);
        AzaleaConfigurationApi.register(newMap);
        return true;
    }

    public boolean renameMapConfigFile(String mapName, String newMapName) {
        if (copyMap(mapName, newMapName)) {
            return deleteMap(mapName);
        }
        return false;
    }

    public boolean deleteMap(String mapName) {
        AzaleaConfigurationApi.unregister(maps.get(mapName));
        return maps.remove(mapName) != null;
    }
}
