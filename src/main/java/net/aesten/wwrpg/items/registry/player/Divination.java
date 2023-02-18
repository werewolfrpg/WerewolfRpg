package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.ShopWerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class Divination extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "divination";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.HEART_OF_THE_SEA, 1)
                .addName(ChatColor.GOLD + "Divination")
                .addLore(ChatColor.GREEN + "The item will disappear when purchased")
                .addLore(ChatColor.GREEN + "It will add 1 to your divination counter")
                .addLore(ChatColor.BLUE + "Unveil the identity of a player")
                .addEnchantment(Enchantment.LUCK, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
                .build();
    }
    
    @Override
    public Integer getDefaultCost() {
        return 6;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 0;
    }
}
