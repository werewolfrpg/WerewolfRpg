package net.aesten.werewolfmc.plugin.skeleton;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.*;

public class SkeletonManager implements Listener {
    public void summonBasicSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("basic_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getConfig().getBasicSkeletonHealth().get());
        skeleton.setHealth(WerewolfGame.getConfig().getBasicSkeletonHealth().get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getConfig().getBasicSkeletonDamage().get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonLuckySkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("lucky_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getConfig().getLuckySkeletonHealth().get());
        skeleton.setHealth(WerewolfGame.getConfig().getLuckySkeletonHealth().get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.GOLDEN_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getConfig().getLuckySkeletonDamage().get());
        skeleton.setRemoveWhenFarAway(false);
        skeleton.setLootTable(null);
    }

    public void summonSpecialSkeleton(World world, Vector coordinates) {
        Skeleton skeleton = (Skeleton) world.spawnEntity(coordinates.toLocation(world), EntityType.SKELETON);
        skeleton.addScoreboardTag("special_skeleton");
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_MAX_HEALTH)).setBaseValue(WerewolfGame.getConfig().getSpecialSkeletonHealth().get());
        skeleton.setHealth(WerewolfGame.getConfig().getSpecialSkeletonHealth().get());
        Objects.requireNonNull(skeleton.getEquipment()).setItemInMainHand(null);
        Objects.requireNonNull(skeleton.getEquipment()).setHelmet(new ItemStack(Material.DIAMOND_HELMET, 1));
        Objects.requireNonNull(skeleton.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE)).setBaseValue(WerewolfGame.getConfig().getSpecialSkeletonDamage().get());
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

        for (int i = 0 ; i < WerewolfGame.getInstance().getParticipants().size() * WerewolfGame.getConfig().getBasicSkeletonSpawnNumberPerPlayer().get() ; i++) {
            summonBasicSkeleton(map.getWorld(), spawns.get(i));
        }

        if (WerewolfGame.getConfig().getLuckySkeletonEnable().get()) {
            for (int i = 0 ; i < WerewolfGame.getConfig().getLuckySkeletonMaxSpawnNumber().get() ; i++) {
                if (rnd.nextDouble() < WerewolfGame.getConfig().getLuckySkeletonSpawnChance().get()) {
                    summonLuckySkeleton(map.getWorld(), spawns.get(rnd.nextInt(spawns.size())));
                }
            }
        }

        if (WerewolfGame.getConfig().getSpecialSkeletonEnable().get()) {
            for (int i = 0 ; i < WerewolfGame.getConfig().getSpecialSkeletonMaxSpawnNumber().get() ; i++) {
                if (rnd.nextDouble() < WerewolfGame.getConfig().getSpecialSkeletonSpawnChance().get()) {
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
                if (entity.getScoreboardTags().contains("basic_skeleton") && rnd.nextDouble() < WerewolfGame.getConfig().getBasicSkeletonEmeraldDropRate().get()) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, 1));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledBasicSkeletons();
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addBasicSkeletonEmeraldDrops();
                }
                else if (entity.getScoreboardTags().contains("lucky_skeleton")) {
                    player.getInventory().addItem(new ItemStack(Material.EMERALD, WerewolfGame.getConfig().getLuckySkeletonEmeraldDropNumber().get()));
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledLuckySkeletons();
                }
                else if (entity.getScoreboardTags().contains("special_skeleton")) {
                    ShopWerewolfItem drop = WerewolfGame.getConfig().getSpecialSkeletonDrops().get().get(rnd.nextInt(WerewolfGame.getConfig().getSpecialSkeletonDrops().get().size()));
                    player.getInventory().addItem(drop.getItem());
                    WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addKilledSpecialSkeletons();
                }
            }
        }
    }
}
