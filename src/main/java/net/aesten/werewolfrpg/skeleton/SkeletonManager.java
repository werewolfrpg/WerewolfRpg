package net.aesten.werewolfrpg.skeleton;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.items.base.WerewolfItem;
import net.aesten.werewolfrpg.items.registry.PlayerItem;
import net.aesten.werewolfrpg.map.WerewolfMap;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.*;
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
    private final Property<Integer> basicSkeletonSpawnNumberPerPlayer = Property.create("skeleton.basic.spawn_number_per_player", PropertyType.INTEGER, () -> 12)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> basicSkeletonDamage = Property.create("skeleton.basic.damage", PropertyType.DOUBLE, () -> 4.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> basicSkeletonHealth = Property.create("skeleton.basic.health", PropertyType.DOUBLE, () -> 12.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> basicSkeletonEmeraldDropRate = Property.create("skeleton.basic.emerald_drop_rate", PropertyType.DOUBLE, () -> 0.5)
            .addPolicy(PROPORTION)
            .done();

    //lucky skeleton properties
    private final Property<Boolean> luckySkeletonEnable = Property.create("skeleton.lucky.enable", PropertyType.BOOLEAN, () -> true)
            .done();
    private final Property<Integer> luckySkeletonMaxSpawnNumber = Property.create("skeleton.lucky.max_spawn_number", PropertyType.INTEGER, () -> 4)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> luckySkeletonSpawnChance = Property.create("skeleton.lucky.spawn_chance", PropertyType.DOUBLE, () -> 0.5)
            .addPolicy(PROPORTION)
            .done();
    private final Property<Double> luckySkeletonDamage = Property.create("skeleton.lucky.damage", PropertyType.DOUBLE, () -> 3.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> luckySkeletonHealth = Property.create("skeleton.lucky.health", PropertyType.DOUBLE, () -> 20.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Integer> luckySkeletonEmeraldDropNumber = Property.create("skeleton.lucky.emerald_drop_number", PropertyType.INTEGER, () -> 2)
            .addPolicy(POSITIVE_INTEGER)
            .done();

    //special skeleton properties
    private final Property<Boolean> specialSkeletonEnable = Property.create("skeleton.special.enable", PropertyType.BOOLEAN, () -> true)
            .done();
    private final Property<Integer> specialSkeletonMaxSpawnNumber = Property.create("skeleton.special.max_spawn_number", PropertyType.INTEGER, () -> 1)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> specialSkeletonSpawnChance = Property.create("skeleton.special.spawn_chance", PropertyType.DOUBLE, () -> 0.4)
            .addPolicy(PROPORTION)
            .done();
    private final Property<Double> specialSkeletonDamage = Property.create("skeleton.special.damage", PropertyType.DOUBLE, () -> 5.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> specialSkeletonHealth = Property.create("skeleton.special.health", PropertyType.DOUBLE, () -> 32.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final ListProperty<ShopWerewolfItem> specialSkeletonDrops = ListProperty.create("skeleton.special.drop_table", WEREWOLF_ITEM, () -> defaultSpecialSkeletonDropTable)
            .done();

    public void summonBasicSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("basic_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getSkeletonManager().basicSkeletonHealth.get());
        skeleton.setHealth(WerewolfGame.getSkeletonManager().basicSkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().basicSkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonLuckySkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("lucky_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getSkeletonManager().luckySkeletonHealth.get());
        skeleton.setHealth(WerewolfGame.getSkeletonManager().luckySkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().luckySkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonSpecialSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("special_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getSkeletonManager().specialSkeletonHealth.get());
        skeleton.setHealth(WerewolfGame.getSkeletonManager().specialSkeletonHealth.get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getSkeletonManager().specialSkeletonDamage.get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonOnlyBasics(WerewolfMap map) {
        map.getSkeletonSpawnLocations().forEach(spawn -> summonBasicSkeleton(map.getWorld(), spawn));
    }

    public void summonAllSkeletons(WerewolfMap map) {
        Random rnd = new Random();
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
        Random rnd = new Random();
        LivingEntity entity = event.getEntity();
        if (entity.getType() == EntityType.SKELETON) {
            if (entity.getKiller() != null) {
                Player player = entity.getKiller();
                if (entity.getScoreboardTags().contains("basic_skeleton") && rnd.nextDouble() < basicSkeletonEmeraldDropRate.get()) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledBasicSkeletons();
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addBasicSkeletonEmeraldDrops();
                }
                else if (entity.getScoreboardTags().contains("lucky_skeleton")) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, luckySkeletonEmeraldDropNumber.get()));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledLuckySkeletons();
                }
                else if (entity.getScoreboardTags().contains("special_skeleton")) {
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
