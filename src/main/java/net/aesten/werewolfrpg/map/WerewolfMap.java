package net.aesten.werewolfrpg.map;

import net.aesten.werewolfrpg.core.WerewolfGame;
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
    private final String name;
    private final Property<World> world;
    private final Property<Location> mapSpawn;
    private final Property<Vector> borderCenter;
    private final Property<Double> borderSize;
    private final ListProperty<Vector> skullLocations;
    private final ListProperty<Vector> skeletonSpawnLocations;

    private static final AssignmentPolicy<Double> POSITIVE_DOUBLE = AssignmentPolicy.create(d -> d > 0, "World border cannot be smaller than 0");

    public WerewolfMap(String mapName, World world) {
        this.name = mapName;
        this.world = Property.create("world", CUSTOM_WORLD, () -> world).done();
        this.mapSpawn = Property.create("spawn", PropertyType.LOCATION, world::getSpawnLocation).done();
        this.borderCenter = Property.create("border.center", PropertyType.VECTOR, () -> world.getWorldBorder().getCenter().toVector()).done();
        this.borderSize = Property.create("border.size", PropertyType.DOUBLE, () -> world.getWorldBorder().getSize()).addPolicy(POSITIVE_DOUBLE).done();
        this.skullLocations = ListProperty.create("player_head_positions", PropertyType.VECTOR, ArrayList::new).done();
        this.skeletonSpawnLocations = ListProperty.create("skeleton_spawn_locations", PropertyType.VECTOR, ArrayList::new).done();
    }

    public WerewolfMap(WerewolfMap map) {
        this.name = map.name;
        this.world = map.world;
        this.mapSpawn = map.mapSpawn;
        this.borderCenter = map.borderCenter;
        this.borderSize = map.borderSize;
        this.skullLocations = map.skullLocations;
        this.skeletonSpawnLocations = map.skeletonSpawnLocations;
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
        return name;
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(world, mapSpawn, borderCenter, borderSize, skullLocations, skeletonSpawnLocations);
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
