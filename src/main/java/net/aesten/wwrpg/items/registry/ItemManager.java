package net.aesten.wwrpg.items.registry;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.models.*;
import net.aesten.wwrpg.items.registry.player.*;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;

public class ItemManager implements Listener {
    private static final Map<String, WerewolfItem> registry = new HashMap<>();

    public static Map<String, WerewolfItem> getRegistry() {
        return registry;
    }

    public static WerewolfItem getItemFromId(String id) {
        return registry.get(id);
    }

    public static void initRegistry() {
        SkeletonPunisher skeletonPunisher = new SkeletonPunisher();
        registry.put(skeletonPunisher.getId(), skeletonPunisher);

        ExquisiteMeat exquisiteMeat = new ExquisiteMeat();
        registry.put(exquisiteMeat.getId(), exquisiteMeat);

        HuntersBow huntersBow = new HuntersBow();
        registry.put(huntersBow.getId(), huntersBow);

        SharpArrow sharpArrow = new SharpArrow();
        registry.put(sharpArrow.getId(), sharpArrow);

        StunGrenade stunGrenade = new StunGrenade();
        registry.put(stunGrenade.getId(), stunGrenade);

        WerewolfAxe werewolfAxe = new WerewolfAxe();
        registry.put(werewolfAxe.getId(), werewolfAxe);

        SkeletonSlicer skeletonSlicer = new SkeletonSlicer();
        registry.put(skeletonSlicer.getId(), skeletonSlicer);

        InvisibilityPotion invisibilityPotion = new InvisibilityPotion();
        registry.put(invisibilityPotion.getId(), invisibilityPotion);

        SwiftnessPotion swiftnessPotion = new SwiftnessPotion();
        registry.put(swiftnessPotion.getId(), swiftnessPotion);

        LightOfRevelation lightOfRevelation = new LightOfRevelation();
        registry.put(lightOfRevelation.getId(), lightOfRevelation);

        Protection protection = new Protection();
        registry.put(protection.getId(), protection);

        Divination divination = new Divination();
        registry.put(divination.getId(), divination);

        TraitorsGuide traitorsGuide = new TraitorsGuide();
        registry.put(traitorsGuide.getId(), traitorsGuide);

        HolyStar holyStar = new HolyStar();
        registry.put(holyStar.getId(), holyStar);

        SneakNotice sneakNotice = new SneakNotice();
        registry.put(sneakNotice.getId(), sneakNotice);

        AshOfTheDead ashOfTheDead = new AshOfTheDead();
        registry.put(ashOfTheDead.getId(), ashOfTheDead);

        CurseSpear curseSpear = new CurseSpear();
        registry.put(curseSpear.getId(), curseSpear);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<WerewolfItem> werewolfItem = registry.values().stream().filter(item -> item.getItem().getItemMeta() == Objects.requireNonNull(event.getItem()).getItemMeta()).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof InteractItem interactItem) {
                interactItem.onPlayerInteract(event);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            ItemStack eventItem = player.getInventory().getItemInMainHand();
            Optional<WerewolfItem> werewolfItem = registry.values().stream().filter(item -> item.getItem().getItemMeta() == Objects.requireNonNull(eventItem).getItemMeta()).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof EntityDamageItem entityDamageItem) {
                entityDamageItem.onEntityDamage(event);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile instanceof Arrow && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) registry.get("sharp_arrow")).onProjectileHit(event);
        }
        else if (projectile instanceof Snowball && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) registry.get("stun_grenade")).onProjectileHit(event);
        }
        else if (projectile instanceof Trident && projectile.hasMetadata("werewolf_projectile")) {
            ((ProjectileItem) registry.get("curse_spear")).onProjectileHit(event);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        ((PlayerMoveItem) registry.get("stun_grenade")).onPlayerMove(event);
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player player && WerewolfGame.getInstance().isParticipant(player)) {
            event.getEntity().setMetadata("werewolf_projectile", new FixedMetadataValue(WerewolfRpg.getPlugin(), 1));
        }
    }
}
