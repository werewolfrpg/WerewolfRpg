package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.items.base.*;
import net.aesten.werewolfrpg.shop.ShopType;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

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
                handleTrident(game, target);
            }
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Projectile projectile = event.getEntity();
        if (event.getHitEntity() instanceof Player player && event.getEntity().getShooter() instanceof Player) {
            handleTrident(game, player);
        }
        projectile.remove();
    }

    private void handleTrident(WerewolfGame game, Player target) {
        World world = game.getMap().getWorld();
        WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());

        if (data.isCursed() && data.getRole() != Role.VAMPIRE) {
            if (game.isNight() && data.hasActiveProtection()) {
                data.setHasActiveProtection(false);
                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
            } else {
                world.playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f,1);
                target.setHealth(0);
            }
        } else {
            world.playSound(target.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f,1);
            data.setCursed(true);
        }
    }
}
