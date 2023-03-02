package net.aesten.wwrpg.map;

import net.aesten.wwrpg.core.WerewolfGame;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

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

    private static final AssignmentPolicy<Double> POSITIVE_DOUBLE = AssignmentPolicy.create(d -> d > 0, "World border cannot be smaller than 0");

    public WerewolfMap(String mapName, World world) {
        this.mapName = new Property<>(PropertyType.STRING, () -> mapName, "map_name", "name of the map", false);
        this.world = new Property<>(CUSTOM_WORLD, () -> world, "world", "the world which contains the map", false);
        this.mapSpawn = new Property<>(PropertyType.LOCATION, world::getSpawnLocation, "spawn", "the spawn point for this map", false);
        this.borderCenter = new Property<>(PropertyType.VECTOR, () -> world.getWorldBorder().getCenter().toVector(), "border.center","the center point for the world border when playing this map",false);
        this.borderSize = new Property<>(PropertyType.DOUBLE, () -> world.getWorldBorder().getSize(), "border.size", "the world border size when playing this map", false, POSITIVE_DOUBLE);
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


    public void updateMapName(String mapName) {
        this.mapName.set(mapName);
    }

    public void updateWorld(World world) {
        this.world.set(world);
    }

    public void updateMapSpawn(Location mapSpawn) {
        this.mapSpawn.set(mapSpawn);
    }

    public void updateBorderCenter(Vector borderCenter) {
        this.borderCenter.set(borderCenter);
    }

    public void updateBorderSize(Double borderSize) {
        this.borderSize.set(borderSize);
    }


    @Override
    public String getName() {
        return mapName.get();
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(mapName, world, mapSpawn, borderCenter, borderSize, skullLocations, skeletonSpawnLocations);
    }


    private static final PropertyType<World> CUSTOM_WORLD = new PropertyType<>(World.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable World currentValue) {
            return WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().stream().toList();
        }

        @Override
        public World parse(CommandSender sender, Arguments arguments, @Nullable World currentValue) {
            return WerewolfGame.getMapManager().getWorldManager().getWorldFromName(arguments.getLast());
        }

        @Override
        public Object serialize(World object) {
            return object.getName();
        }

        @Override
        public World deserialize(Object object) {
            return Bukkit.getWorld((String) object);
        }

        @Override
        public String print(World object) {
            return object.getName();
        }
    };

}
