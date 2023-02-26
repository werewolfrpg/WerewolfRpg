package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.ShopWerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class HuntersBow extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "hunters_bow";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.BOW, 1)
                .addName(ChatColor.GOLD + "Hunter's Bow")
                .addLore(ChatColor.BLUE + "A one-shot bow")
                .addFlags(ItemFlag.HIDE_ATTRIBUTES)
                .addDamage(384)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 2;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.BASIC;
    }

    @Override
    public Integer getShopSlot() {
        return 2;
    }
}
