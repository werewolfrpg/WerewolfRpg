package net.aesten.werewolfrpg.plugin.items.registry.player;

import net.aesten.werewolfrpg.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.plugin.shop.ShopType;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class InvisibilityPotion extends ShopWerewolfItem {
    @Override
    public String getId() {
        return "invisibility_potion";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.POTION, 1)
                .addName(ChatColor.GOLD + "Invisibility Potion")
                .addLore(ChatColor.BLUE + "Become invisible")
                .addLore(ChatColor.GRAY + "Active for 20 seconds")
                .addPotionEffect(PotionEffectType.INVISIBILITY, 400, 0, false, false, true)
                .setPotionColor(Color.AQUA)
                .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 4;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 4;
    }
}
