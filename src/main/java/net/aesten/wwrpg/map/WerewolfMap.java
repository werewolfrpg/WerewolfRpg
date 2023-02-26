package net.aesten.wwrpg.map;

import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.ListProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class WerewolfMap implements Configurable {
    private Property<String> mapName;
    private Property<World> world;
    private Property<Location> mapSpawn;
    private Property<Vector> borderCenter;
    private Property<Double> borderSize;
    private ListProperty<Vector> skullLocations;
    private ListProperty<Vector> skeletonSpawnLocations;

    public WerewolfMap(String mapName, World world) {
        this.mapName = new Property<>(PropertyType.STRING, () -> mapName, "map_name", "name of the map", false);
        this.world = new Property<>(PropertyType.WORLD, () -> world, "world", "the world which contains the map", false);
        this.mapSpawn = new Property<>(PropertyType.LOCATION, world::getSpawnLocation, "spawn", "the spawn point for this map", false);
        this.borderCenter = new Property<>(PropertyType.VECTOR, () -> world.getWorldBorder().getCenter().toVector(), "border.center","the center point for the world border when playing this map",false);
        this.borderSize = new Property<>(PropertyType.DOUBLE, () -> world.getWorldBorder().getSize(), "border.size", "the world border size when playing this map", false);
        this.skullLocations = new ListProperty<>(PropertyType.VECTOR, ArrayList::new, "player_head_positions", "The coordinates of the player head blocks", false);
        this.skeletonSpawnLocations = new ListProperty<>(PropertyType.VECTOR, ArrayList::new, "skeleton_spawn_locations", "The coordinates of skeleton spawn points", false);
    }

    public WerewolfMap(WerewolfMap map) {
        this.mapName = map.mapName;
        this.world = map.world;
        this.mapSpawn = map.mapSpawn;
        this.borderCenter = map.borderCenter;
        this.borderSize = map.borderSize;
        this.skullLocations = map.skullLocations;
        this.skeletonSpawnLocations = map.skeletonSpawnLocations;
    }

    public WerewolfMap() {}

    public String getMapName() {
        return mapName.get();
    }

    public List<Vector> getSkullLocations() {
        return skullLocations.get();
    }

    public List<Vector> getSkeletonSpawnLocations() {
        return skeletonSpawnLocations.get();
    }

    public World getWorld() {
        return world.get();
    }

    public Location getMapSpawn() {
        return mapSpawn.get();
    }

    public Vector getBorderCenter() {
        return borderCenter.get();
    }

    public Double getBorderSize() {
        return borderSize.get();
    }

    @Override
    public String getName() {
        return mapName.get();
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(mapName, world, mapSpawn, borderCenter, borderSize, skullLocations, skeletonSpawnLocations);
    }


}
