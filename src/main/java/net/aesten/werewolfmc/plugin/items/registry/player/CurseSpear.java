package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import net.aesten.werewolfmc.plugin.items.base.*;
import net.aesten.werewolfmc.plugin.shop.ShopType;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;

public class CurseSpear extends ShopWerewolfItem implements EntityDamageItem, ProjectileItem {
    @Override
    public String getId() {
        return "curse_spear";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.TRIDENT, 1)
                .addName(ChatColor.GOLD + "Curse Spear")
                .addLore(ChatColor.GREEN + "Hit or throw to enemy to use")
                .addLore(ChatColor.BLUE + "First hit on a player curses him")
                .addLore(ChatColor.BLUE + "Hitting a cursed player will kill him")
                .addLore(ChatColor.GRAY + "Protection and Vampire can negate second hit")
                .addLore(ChatColor.GRAY + "Breaks on usage")
                .addEnchantment(Enchantment.LOYALTY, 0)
                .addDamage(250)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 3;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.BASIC;
    }

    @Override
    public Integer getShopSlot() {
        return 6;
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
            if (damager.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT)) {
                World world = game.getMap().getWorld();
                WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());

                if (data.isCursed()) {
                    if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                        game.getTracker().getPlayerStats(damager.getUniqueId()).addCurseSpearMeleeUsed(false, false);
                        return;
                    }
                    if (game.isNight() && data.hasActiveProtection()) {
                        data.setHasActiveProtection(false);
                        target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
                        game.getTracker().getPlayerStats(damager.getUniqueId()).addCurseSpearMeleeUsed(false, false);
                        game.getTracker().getPlayerStats(target.getUniqueId()).addProtectionTriggered();
                    }
                    else {
                        world.playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f,1);
                        target.setHealth(0);
                        game.getTracker().getPlayerStats(damager.getUniqueId()).addCurseSpearMeleeUsed(false, true);
                        game.getTracker().getPlayerStats(damager.getUniqueId()).addKills();
                        game.getTracker().getSpecificDeathCauses().put(target.getUniqueId(), new AbstractMap.SimpleEntry<>("curse_spear_melee", damager.getUniqueId()));
                    }
                }
                else {
                    world.playSound(target.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f,1);
                    data.setCursed(true);
                    game.getTracker().getPlayerStats(damager.getUniqueId()).addCurseSpearMeleeUsed(true, false);
                }
            }
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Projectile projectile = event.getEntity();
        if (event.getHitEntity() instanceof Player player && event.getEntity().getShooter() instanceof Player shooter) {
            World world = game.getMap().getWorld();
            WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());

            if (data.isCursed()) {
                if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                    game.getTracker().getPlayerStats(shooter.getUniqueId()).addCurseSpearThrowHits(false, false);
                    return;
                }
                if (game.isNight() && data.hasActiveProtection()) {
                    data.setHasActiveProtection(false);
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    WerewolfUtil.sendPluginText(player, "Protection was activated", ChatColor.GREEN);
                    game.getTracker().getPlayerStats(shooter.getUniqueId()).addCurseSpearThrowHits(false, false);
                    game.getTracker().getPlayerStats(player.getUniqueId()).addProtectionTriggered();
                }
                else {
                    world.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f,1);
                    player.setHealth(0);
                    game.getTracker().getPlayerStats(shooter.getUniqueId()).addCurseSpearThrowHits(false, true);
                    game.getTracker().getPlayerStats(shooter.getUniqueId()).addKills();
                    game.getTracker().getSpecificDeathCauses().put(player.getUniqueId(), new AbstractMap.SimpleEntry<>("curse_spear_throw", shooter.getUniqueId()));
                }
            }
            else {
                world.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f,1);
                data.setCursed(true);
                game.getTracker().getPlayerStats(shooter.getUniqueId()).addCurseSpearThrowHits(true, false);
            }
        }
        projectile.remove();
    }
}
