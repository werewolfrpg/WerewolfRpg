package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.items.base.EntityDamageItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class WerewolfAxe extends ShopWerewolfItem implements EntityDamageItem {
    @Override
    public String getId() {
        return "werewolf_axe";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.NETHERITE_AXE, 1)
                .addName(ChatColor.DARK_RED + "Werewolf Axe")
                .addLore(ChatColor.DARK_PURPLE + "The murderous axe of werewolves")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addDamage(2031)
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

            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                event.setCancelled(true);
            } else if (game.isNight() && data.hasActiveProtection()) {
                event.setCancelled(true);
                data.setHasActiveProtection(false);
                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
            } else {
                target.setHealth(0);
            }
        }
    }
}
