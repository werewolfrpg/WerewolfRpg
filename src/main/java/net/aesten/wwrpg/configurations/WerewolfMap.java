package net.aesten.wwrpg.configurations;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WerewolfMap {
    private String mapName;
    private final World world;
    private final List<Vector> signPostLocations;
    private final List<Vector> skeletonSpawnLocations;
    private Location mapSpawn;
    private Location borderCenter;
    private Double borderSize;

    public WerewolfMap(String mapName, World world) {
        this.mapName = mapName;
        this.world = world;
        this.signPostLocations = new ArrayList<>();
        this.skeletonSpawnLocations = new ArrayList<>();
        this.mapSpawn = world.getSpawnLocation();
        this.borderCenter = world.getWorldBorder().getCenter();
        this.borderSize = world.getWorldBorder().getSize();
    }

    public void addSkeletonSpawn(Vector vector) {
        skeletonSpawnLocations.add(vector);
    }

    public void addSignPost(Vector vector) {
        signPostLocations.add(vector);
    }

    public void removeSkeletonSpawn(Vector vector) {
        skeletonSpawnLocations.remove(vector);
    }

    public void removeSignPost(Vector vector) {
        signPostLocations.remove(vector);
    }

    public void wipeSkeletonSpawns() {
        skeletonSpawnLocations.clear();
    }

    public void wipeSignPosts() {
        signPostLocations.clear();
    }

    public String getMapName() {
        return mapName;
    }

    public List<Vector> getSignPostLocations() {
        return signPostLocations;
    }

    public List<Vector> getSkeletonSpawnLocations() {
        return skeletonSpawnLocations;
    }

    public World getWorld() {
        return world;
    }

    public Location getMapSpawn() {
        return mapSpawn;
    }

    public Location getBorderCenter() {
        return borderCenter;
    }

    public Double getBorderSize() {
        return borderSize;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public void setMapSpawn(Location mapSpawn) {
        this.mapSpawn = mapSpawn;
    }

    public void setBorderCenter(Location borderCenter) {
        this.borderCenter = borderCenter;
    }

    public void setBorderSize(Double borderSize) {
        this.borderSize = borderSize;
    }

}
