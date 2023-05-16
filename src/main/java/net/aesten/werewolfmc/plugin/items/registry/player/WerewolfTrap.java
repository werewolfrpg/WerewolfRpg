package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class WerewolfTrap extends WerewolfItem implements InteractItem {
    private static final List<BukkitTask> toCancel = new ArrayList<>();
    private static final Map<Integer, Boolean> completed = new HashMap<>();

    @Override
    public String getId() {
        return "werewolf_trap";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.WITHER_ROSE, 1)
                .addName(ChatColor.DARK_RED + "Werewolf Trap")
                .addLore(ChatColor.GREEN + "Click a block to use")
                .addLore(ChatColor.BLUE + "Sets up a trap at the clicked position")
                .addLore(ChatColor.BLUE + "Only werewolves can see the trap circle")
                .addLore(ChatColor.BLUE + "Other roles can trigger the trap")
                .addLore(ChatColor.BLUE + "Once triggered, wither skeletons are summoned")
                .addLore(ChatColor.GRAY + "Can only be obtained in a Werewolf Night")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player user = event.getPlayer();
            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
            Location loc = Objects.requireNonNull(event.getClickedBlock()).getLocation().add(0,1,0);
            BukkitTask task = new BukkitRunnable() {
                @Override
                public void run() {
                    List<Player> players = game
                            .getMap()
                            .getWorld()
                            .getNearbyEntities(loc, 3, 3, 3)
                            .stream()
                            .filter(WerewolfTrap::isNonWerewolfAlivePlayers)
                            .map(Player.class::cast)
                            .toList();
                    if (players.size() > 0 && !completed.get(getTaskId())) {
                        completed.put(getTaskId(), true);
                        WerewolfUtil.getSpawnSpacesAround(loc, 3, 5)
                                .forEach(WerewolfTrap::summonWitherSkeleton);
                        players.forEach(p -> p.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1200, 1, false)));

                        this.cancel();
                    } else {
                        WerewolfGame.getTeamsManager().getFaction(Role.WEREWOLF).getPlayers().forEach(werewolf -> WerewolfUtil.spawnCircleParticles(werewolf, loc, 3, 300));
                    }
                }
            }.runTaskTimer(WerewolfPlugin.getPlugin(), 0, 5);
            toCancel.add(task);
            completed.put(task.getTaskId(), false);
        } else {
            WerewolfUtil.sendErrorText(event.getPlayer(), "Click on a block to use");
        }
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
