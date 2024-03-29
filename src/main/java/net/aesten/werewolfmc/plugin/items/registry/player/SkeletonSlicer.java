package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SkeletonSlicer extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "skeleton_slicer";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.WOODEN_SWORD, 1)
                .addName(ChatColor.GOLD + "Skeleton Slicer")
                .addLore(ChatColor.BLUE + "Kill skeletons faster")
                .addLore(ChatColor.GRAY + "Has to be purchased every night")
                .addLore(ChatColor.GRAY + "Deleted from inventory at the end of night time")
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 10)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
                .setUnbreakable()
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
        return 1;
    }
}
