package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import net.aesten.werewolfmc.plugin.items.base.EntityDamageItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.shop.ShopType;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;

public class WerewolfAxe extends ShopWerewolfItem implements EntityDamageItem {
    @Override
    public String getId() {
        return "werewolf_axe";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.STONE_AXE, 1)
                .addName(ChatColor.DARK_RED + "Werewolf Axe")
                .addLore(ChatColor.DARK_PURPLE + "The murderous axe of werewolves")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addDamage(131)
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
        return 5;
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
            game.getMap().getWorld().playSound(damager.getLocation(), Sound.ITEM_TOTEM_USE, 0.6f, 1);
            WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());
            ItemStack item = damager.getInventory().getItemInMainHand();
            item.setAmount(item.getAmount() - 1);

            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                event.setCancelled(true);
                game.getTracker().getPlayerStats(damager.getUniqueId()).addWerewolfAxeUsed(false);
            }
            else if (game.isNight() && data.hasActiveProtection()) {
                event.setCancelled(true);
                data.setHasActiveProtection(false);
                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
                game.getTracker().getPlayerStats(damager.getUniqueId()).addWerewolfAxeUsed(false);
                game.getTracker().getPlayerStats(target.getUniqueId()).addProtectionTriggered();
            }
            else {
                target.setHealth(0);
                game.getTracker().getPlayerStats(damager.getUniqueId()).addWerewolfAxeUsed(true);
                game.getTracker().getPlayerStats(damager.getUniqueId()).addKills();
                game.getTracker().getSpecificDeathCauses().put(target.getUniqueId(), new AbstractMap.SimpleEntry<>("Werewolf Axe", damager.getUniqueId()));
            }
        }
    }
}
