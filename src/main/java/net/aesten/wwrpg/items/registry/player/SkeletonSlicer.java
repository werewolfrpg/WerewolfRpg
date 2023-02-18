package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.ShopWerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
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
                .addLore(ChatColor.BLUE + "Kills skeletons faster")
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 5)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
                .addDamage(19)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 4;
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
