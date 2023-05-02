package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.ProjectileItem;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class WerewolfTrap extends WerewolfItem implements ProjectileItem {
    private static final List<BukkitTask> toCancel = new ArrayList<>();

    @Override
    public String getId() {
        return "werewolf_trap";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.EGG, 1)
                .addName(ChatColor.DARK_RED + "Werewolf Trap")
                .addLore(ChatColor.GREEN + "Throw to use")
                .addLore(ChatColor.BLUE + "Sets up a trap at the landing position")
                .addLore(ChatColor.BLUE + "Only werewolves can see the trap circle")
                .addLore(ChatColor.BLUE + "Other roles can trigger the trap")
                .addLore(ChatColor.BLUE + "Once triggered, wither skeletons are summoned")
                .addLore(ChatColor.GRAY + "Can only be obtained in a Werewolf Night")
                .build();
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Location landingPosition;
        if (event.getHitEntity() instanceof Player player) {
            landingPosition = player.getLocation();
        } else if (event.getHitBlock() != null) {
            landingPosition = event.getHitBlock().getLocation();
        } else {
            return;
        }
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = game
                        .getMap()
                        .getWorld()
                        .getNearbyEntities(landingPosition, 3, 3, 3)
                        .stream()
                        .filter(WerewolfTrap::isNonWerewolfAlivePlayers)
                        .map(Player.class::cast)
                        .toList();
                if (players.size() > 0) {
                    WerewolfUtil.getSpawnSpacesAround(landingPosition, 3, 5)
                            .forEach(WerewolfTrap::summonWitherSkeleton);
                    players.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1, false)));
                    this.cancel();
                } else {
                    WerewolfGame.getTeamsManager().getFaction(Role.WEREWOLF).getPlayers().forEach(werewolf -> WerewolfUtil.spawnCircleParticles(werewolf, landingPosition, 3, 300));
                }
            }
        }.runTaskTimer(WerewolfPlugin.getPlugin(), 0, 5);
        toCancel.add(task);
    }

    private static boolean isNonWerewolfAlivePlayers(Entity entity) {
        if (entity instanceof Player player) {
            return !WerewolfGame.getTeamsManager().getFaction(Role.WEREWOLF).getPlayers().contains(player);
        }
        return false;
    }

    private static void summonWitherSkeleton(Location location) {
        if (location.getWorld() == null) return;
        WitherSkeleton witherSkeleton = (WitherSkeleton) location.getWorld().spawnEntity(location, EntityType.WITHER_SKELETON);
        witherSkeleton.addScoreboardTag("wither_skeleton");
        witherSkeleton.setRemoveWhenFarAway(false);
        witherSkeleton.setLootTable(null);
    }

    public static void clearTasks() {
        toCancel.forEach(BukkitTask::cancel);
        toCancel.clear();
    }
}
