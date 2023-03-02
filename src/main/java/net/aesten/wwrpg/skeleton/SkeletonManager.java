package net.aesten.wwrpg.skeleton;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.base.ShopWerewolfItem;
import net.aesten.wwrpg.items.base.WerewolfItem;
import net.aesten.wwrpg.items.registry.PlayerItem;
import net.aesten.wwrpg.map.WerewolfMap;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SkeletonManager implements Listener, Configurable {
    private static final AssignmentPolicy<Double> POSITIVE_DOUBLE = AssignmentPolicy.create(d -> d > 0, "Negative values are not accepted");
    private static final AssignmentPolicy<Integer> POSITIVE_INTEGER = AssignmentPolicy.create(i -> i > 0, "Negative values are not accepted");
    private static final AssignmentPolicy<Double> PROPORTION = AssignmentPolicy.create(d -> d >= 0 && d <= 1, "Proportions should be between 0 and 1");
    private static final PropertyType<ShopWerewolfItem> WEREWOLF_ITEM = new PropertyType<>(ShopWerewolfItem.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments, @Nullable ShopWerewolfItem currentValue) {
            return WerewolfGame.getShopManager().getAllShopItems().stream().map(ShopWerewolfItem::getId).toList();
        }

        @Override
        public ShopWerewolfItem parse(CommandSender sender, Arguments arguments, @Nullable ShopWerewolfItem currentValue) {
            WerewolfItem item = PlayerItem.getItemFromId(arguments.getLast());
            if (item instanceof ShopWerewolfItem shopItem) return shopItem;
            else return null;
        }

        @Override
        public Object serialize(ShopWerewolfItem object) {
            return object.getId();
        }

        @Override
        public ShopWerewolfItem deserialize(Object object) {
            WerewolfItem item = PlayerItem.getItemFromId((String) object);
            if (item instanceof ShopWerewolfItem shopItem) return shopItem;
            else return null;
        }

        @Override
        public String print(ShopWerewolfItem object) {
            return object.getId();
        }
    };

    private static final List<String> defaultSpecialSkeletonDropTableIds = List.of("ash_of_the_dead", "invisibility_potion", "light_of_revelation", "skeleton_slicer", "stun_grenade", "curse_spear");
    private static final List<ShopWerewolfItem> defaultSpecialSkeletonDropTable = defaultSpecialSkeletonDropTableIds.stream().map(PlayerItem::getItemFromId).filter(item -> item instanceof ShopWerewolfItem).map(ShopWerewolfItem.class::cast).toList();

    //basic skeleton properties
    private final Property<Integer> basicSkeletonSpawnNumberPerPlayer = new Property<>(PropertyType.INTEGER, () -> 12, "skeleton.basic.spawn_number_per_player", "number basic skeletons spawning per player", POSITIVE_INTEGER);
    private final Property<Double> basicSkeletonDamage = new Property<>(PropertyType.DOUBLE, () -> 4.0, "skeleton.basic.damage", "basic skeleton damage", POSITIVE_DOUBLE);
    private final Property<Double> basicSkeletonHealth = new Property<>(PropertyType.DOUBLE, () -> 12.0, "skeleton.basic.health", "basic skeleton health", POSITIVE_DOUBLE);
    private final Property<Double> basicSkeletonEmeraldDropRate = new Property<>(PropertyType.DOUBLE, () -> 0.5, "skeleton.basic.emerald_drop_rate", "skeleton emerald drop rate", PROPORTION);

    //lucky skeleton properties
    private final Property<Boolean> luckySkeletonEnable = new Property<>(PropertyType.BOOLEAN, () -> true, "skeleton.lucky.enable", "enable lucky skeleton spawns");
    private final Property<Integer> luckySkeletonMaxSpawnNumber = new Property<>(PropertyType.INTEGER, () -> 4, "skeleton.lucky.max_spawn_number", "max number of lucky skeletons spawning in one night", POSITIVE_INTEGER);
    private final Property<Double> luckySkeletonSpawnChance = new Property<>(PropertyType.DOUBLE, () -> 0.5, "skeleton.lucky.spawn_chance", "lucky skeleton spawn chance", PROPORTION);
    private final Property<Double> luckySkeletonDamage = new Property<>(PropertyType.DOUBLE, () -> 3.0, "skeleton.lucky.damage", "lucky skeleton damage", POSITIVE_DOUBLE);
    private final Property<Double> luckySkeletonHealth = new Property<>(PropertyType.DOUBLE, () -> 20.0, "skeleton.lucky.health", "lucky skeleton health", POSITIVE_DOUBLE);
    private final Property<Integer> luckySkeletonEmeraldDropNumber = new Property<>(PropertyType.INTEGER, () -> 2, "skeleton.lucky.emerald_drop_number", "number of emeralds dropped by lucky skeleton", POSITIVE_INTEGER);

    //special skeleton properties
    private final Property<Boolean> specialSkeletonEnable = new Property<>(PropertyType.BOOLEAN, () -> true, "skeleton.special.enable", "enable special skeleton spawns");
    private final Property<Integer> specialSkeletonMaxSpawnNumber = new Property<>(PropertyType.INTEGER, () -> 1, "skeleton.special.max_spawn_number", "max number of special skeletons spawning in one night", POSITIVE_INTEGER);
    private final Property<Double> specialSkeletonSpawnChance = new Property<>(PropertyType.DOUBLE, () -> 0.4, "skeleton.special.spawn_chance", "special skeleton spawn chance", PROPORTION);
    private final Property<Double> specialSkeletonDamage = new Property<>(PropertyType.DOUBLE, () -> 5.0, "skeleton.special.damage", "special skeleton damage", POSITIVE_DOUBLE);
    private final Property<Double> specialSkeletonHealth = new Property<>(PropertyType.DOUBLE, () -> 32.0, "skeleton.special.health", "special skeleton health", POSITIVE_DOUBLE);
    private final ListProperty<ShopWerewolfItem> specialSkeletonDrops = new ListProperty<>(WEREWOLF_ITEM, () -> defaultSpecialSkeletonDropTable, "skeleton.special.drop_table", "list of minigame items dropped");


    private final Random rnd = new Random();

    private void summonBasicSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.setCustomName("basic_skeleton");
        skeleton.setCustomNameVisible(false);
        skeleton.setHealth(WerewolfGame.getSkeletonManager().basicSkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().basicSkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    private void summonLuckySkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.setCustomName("lucky_skeleton");
        skeleton.setCustomNameVisible(false);
        skeleton.setHealth(WerewolfGame.getSkeletonManager().luckySkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().luckySkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    private void summonSpecialSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.setCustomName("special_skeleton");
        skeleton.setCustomNameVisible(false);
        skeleton.setHealth(WerewolfGame.getSkeletonManager().specialSkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().specialSkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonAllSkeletons(WerewolfMap map) {
        List<Vector> spawns = map.getSkeletonSpawnLocations();
        Collections.shuffle(spawns);

        for (int i = 0 ; i < WerewolfGame.getInstance().getParticipants().size() * basicSkeletonSpawnNumberPerPlayer.get() ; i++) {
            summonBasicSkeleton(map.getWorld(), spawns.get(i));
        }

        if (luckySkeletonEnable.get()) {
            for (int i = 0 ; i < luckySkeletonMaxSpawnNumber.get() ; i++) {
                if (rnd.nextDouble() < luckySkeletonSpawnChance.get()) {
                    summonLuckySkeleton(map.getWorld(), spawns.get(rnd.nextInt(spawns.size())));
                }
            }
        }

        if (specialSkeletonEnable.get()) {
            for (int i = 0 ; i < specialSkeletonMaxSpawnNumber.get() ; i++) {
                if (rnd.nextDouble() < specialSkeletonSpawnChance.get()) {
                    summonSpecialSkeleton(map.getWorld(), spawns.get(rnd.nextInt(spawns.size())));
                }
            }
        }
    }

    @EventHandler
    public void onSkeletonKill(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.SKELETON) {
            if (entity.getKiller() != null) {
                Player player = entity.getKiller();
                if (Objects.equals(entity.getCustomName(), "basic_skeleton") && rnd.nextDouble() < basicSkeletonEmeraldDropRate.get()) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledBasicSkeletons();
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addBasicSkeletonEmeraldDrops();
                }
                else if (Objects.equals(entity.getCustomName(), "lucky_skeleton")) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, luckySkeletonEmeraldDropNumber.get()));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledLuckySkeletons();
                }
                else if (Objects.equals(entity.getCustomName(), "special_skeleton")) {
                    ShopWerewolfItem drop = specialSkeletonDrops.get().get(rnd.nextInt(specialSkeletonDrops.get().size()));
                    player.getInventory().addItem(drop.getItem());
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledSpecialSkeletons();
                }
            }
        }
    }

    @Override
    public String getName() {
        return "wwrpg-skeleton-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(
                basicSkeletonSpawnNumberPerPlayer,
                basicSkeletonHealth,
                basicSkeletonDamage,
                basicSkeletonEmeraldDropRate,
                luckySkeletonEnable,
                luckySkeletonMaxSpawnNumber,
                luckySkeletonSpawnChance,
                luckySkeletonHealth,
                luckySkeletonDamage,
                luckySkeletonEmeraldDropNumber,
                specialSkeletonEnable,
                specialSkeletonMaxSpawnNumber,
                specialSkeletonSpawnChance,
                specialSkeletonHealth,
                specialSkeletonDamage,
                specialSkeletonDrops
        );
    }
}
