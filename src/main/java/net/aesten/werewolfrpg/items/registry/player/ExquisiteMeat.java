package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ExquisiteMeat extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "exquisite_meat";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.COOKED_BEEF, 5)
                .addName(ChatColor.GOLD + "Exquisite Meat")
                .addLore(ChatColor.BLUE + "Your only food source")
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 1;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.BASIC;
    }

    @Override
    public Integer getShopSlot() {
        return 0;
    }
}