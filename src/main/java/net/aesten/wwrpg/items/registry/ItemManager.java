package net.aesten.wwrpg.items.registry;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.models.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class ItemManager implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<WerewolfItem> werewolfItem = Item.getRegistry().values().stream().filter(item -> item.getItem().getItemMeta() == Objects.requireNonNull(event.getItem()).getItemMeta()).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof InteractItem interactItem) {
                interactItem.onPlayerInteract(event);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack eventItem = player.getInventory().getItemInMainHand();
            Optional<WerewolfItem> werewolfItem = Item.getRegistry().values().stream().filter(item -> item.getItem().getItemMeta() == Objects.requireNonNull(eventItem).getItemMeta()).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof EntityDamageItem entityDamageItem) {
                entityDamageItem.onEntityDamage(event);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) Item.SHARP_ARROW.werewolfItem).onProjectileHit(event);
        }
        else if (projectile instanceof Snowball && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) Item.STUN_GRENADE.werewolfItem).onProjectileHit(event);
        }
        else if (projectile instanceof Trident && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) Item.CURSE_SPEAR.werewolfItem).onProjectileHit(event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ((PlayerMoveItem) Item.STUN_GRENADE.werewolfItem).onPlayerMove(event);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player && WerewolfGame.getInstance().isParticipant(player)) {
            event.getEntity().setMetadata("werewolf_projectile", new FixedMetadataValue(WerewolfRpg.getPlugin(), 1));
        }
    }
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        ItemMeta clickItemMeta = event.getPlayer().getInventory().getItem(event.getHand()).getItemMeta();
        Optional<WerewolfItem> werewolfItem = Item.getRegistry().values().stream().filter(item -> item.getItem().getItemMeta() == clickItemMeta).findAny();
        if (werewolfItem.isPresent() && werewolfItem.get() instanceof EntityInteractItem entityInteractItem) {
            entityInteractItem.onEntityInteract(event);
        }
    }
}
