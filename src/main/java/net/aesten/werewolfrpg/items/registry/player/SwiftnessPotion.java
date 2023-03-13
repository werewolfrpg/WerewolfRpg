package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SwiftnessPotion extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "swiftness_potion";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.POTION, 1)
                .addName(ChatColor.GOLD + "Swiftness Potion")
                .addLore(ChatColor.BLUE + "Minor speed boost during the entire game")
                .addPotionEffect(PotionEffectType.SPEED, 72000, 0, false, false, false)
                .setPotionColor(Color.GRAY)
                .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 1;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 3;
    }
}
