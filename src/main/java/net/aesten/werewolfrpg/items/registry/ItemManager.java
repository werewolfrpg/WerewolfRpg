package net.aesten.werewolfrpg.items.registry;

import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.items.base.*;
import net.aesten.werewolfrpg.tracker.Tracker;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class ItemManager implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND || event.getItem() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<WerewolfItem> werewolfItem = PlayerItem.getRegistry().values().stream().filter(item -> WerewolfUtil.sameItem(item.getItem(), event.getItem())).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof InteractItem interactItem) {
                interactItem.onPlayerInteract(event);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack eventItem = player.getInventory().getItemInMainHand();
            if (eventItem.getType() == Material.AIR) return;
            Optional<WerewolfItem> werewolfItem = PlayerItem.getRegistry().values().stream().filter(item -> WerewolfUtil.sameItem(item.getItem(), eventItem)).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof EntityDamageItem entityDamageItem) {
                entityDamageItem.onEntityDamage(event);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) PlayerItem.SHARP_ARROW.werewolfItem).onProjectileHit(event);
        }
        else if (projectile instanceof Snowball && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) PlayerItem.STUN_GRENADE.werewolfItem).onProjectileHit(event);
        }
        else if (projectile instanceof Trident && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) PlayerItem.CURSE_SPEAR.werewolfItem).onProjectileHit(event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ((PlayerMoveItem) PlayerItem.STUN_GRENADE.werewolfItem).onPlayerMove(event);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player && WerewolfGame.getInstance().isParticipant(player)) {
            event.getEntity().setMetadata("werewolf_projectile", new FixedMetadataValue(WerewolfRpg.getPlugin(), 1));
            if (event.getEntity().getType() == EntityType.ARROW) {
                WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addArrowUsed();
            }
            else if (event.getEntity().getType() == EntityType.SNOWBALL) {
                WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addStunGrenadeUsed();
            }
            else if (event.getEntity().getType() == EntityType.TRIDENT) {
                WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).addCurseSpearThrowUsed();
            }
        }
    }

    @EventHandler
    public void onItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        Tracker tracker = WerewolfGame.getInstance().getTracker();
        if (WerewolfUtil.sameItem(event.getItem(), PlayerItem.getItemFromId("exquisite_meat").getItem())) {
            tracker.getPlayerStats(player.getUniqueId()).addSteaksEaten();
        }
        else if (WerewolfUtil.sameItem(event.getItem(), PlayerItem.getItemFromId("swiftness_potion").getItem())) {
            tracker.getPlayerStats(player.getUniqueId()).addSwiftnessUsed();
        }
        else if (WerewolfUtil.sameItem(event.getItem(), PlayerItem.getItemFromId("invisibility_potion").getItem())) {
            tracker.getPlayerStats(player.getUniqueId()).addInvisibilityUsed();
        }
    }

    @EventHandler
    public void onSkullClick(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        WerewolfGame game = WerewolfGame.getInstance();
        Player player = event.getPlayer();
        WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && Objects.requireNonNull(event.getClickedBlock()).getType() == Material.PLAYER_HEAD) {
            if (!game.isNight()) {
                WerewolfUtil.sendPluginText(player, "You cannot use a divination during day time", ChatColor.RED);
            }
            else if (data.hasAlreadyUsedDivination()) {
                WerewolfUtil.sendPluginText(player, "You have already used a divination this night", ChatColor.RED);
            }
            else if (data.getRemainingDivinations() < 1) {
                WerewolfUtil.sendPluginText(player, "You don't have any divinations left", ChatColor.RED);
            }
            else {
                OfflinePlayer offlinePlayer = ((Skull) event.getClickedBlock().getState()).getOwningPlayer();
                if (offlinePlayer != null && game.isParticipant(Bukkit.getPlayer(offlinePlayer.getUniqueId()))) {
                    WerewolfPlayerData targetData = game.getDataMap().get(offlinePlayer.getUniqueId());
                    if (player.getUniqueId() == offlinePlayer.getUniqueId()) {
                        WerewolfUtil.sendPluginText(player, "You cannot use a divination on yourself", ChatColor.RED);
                    }
                    else {
                        Role role = game.getDataMap().get(offlinePlayer.getUniqueId()).getRole().divinationRole();
                        String name = offlinePlayer.getName();
                        data.setHasAlreadyUsedDivination(true);
                        data.setRemainingDivinations(data.getRemainingDivinations() - 1);
                        targetData.setHasBeenDivinated(true);
                        WerewolfUtil.sendPluginText(player,  name + ChatColor.WHITE + " is a " + role.color + role.name, WerewolfRpg.COLOR);
                        game.getTracker().getPlayerStats(player.getUniqueId()).addDivinationUsed();
                    }
                }
                else {
                    WerewolfUtil.sendPluginText(player, "This is not a valid player!", ChatColor.RED);
                }
            }
        }
    }
}
