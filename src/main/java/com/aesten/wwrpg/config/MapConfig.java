package com.aesten.wwrpg.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;

public class MapConfig {
    private final World world;
    private final List<Location> signPostLocations;
    private final List<Location> skeletonSpawnLocations;

    public MapConfig(MapConfigFile configFile) {
        this.world = Bukkit.getWorld(configFile.getConfig().getString("world"));
        this.signPostLocations = configFile;
        this.skeletonSpawnLocations = configFile;
    }
}
