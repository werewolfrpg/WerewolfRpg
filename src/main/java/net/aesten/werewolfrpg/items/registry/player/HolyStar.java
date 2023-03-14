package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.items.base.EntityDamageItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class HolyStar extends ShopWerewolfItem implements EntityDamageItem {
    @Override
    public String getId() {
        return "holy_star";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.NETHER_STAR, 1)
                .addName(ChatColor.GOLD + "Holy Star")
                .addLore(ChatColor.GREEN + "Hit a player to use")
                .addLore(ChatColor.BLUE + "Can kill a vampire even during night time")
                .addLore(ChatColor.GRAY + "When used on non-vampire item is lost")
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 2;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 6;
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
            ItemStack item = damager.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.NETHER_STAR)) {
                game.getMap().getWorld().playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 1,1);
                item.setAmount(item.getAmount() - 1);

                if (game.getDataMap().get(target.getUniqueId()).getRole() == Role.VAMPIRE) {
                    target.setHealth(0);
                    game.getTracker().getPlayerStats(damager.getUniqueId()).addHolyStarUsed(true);
                }
                else {
                    game.getTracker().getPlayerStats(damager.getUniqueId()).addHolyStarUsed(false);
                }
            }
        }
    }
}