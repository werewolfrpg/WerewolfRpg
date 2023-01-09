package com.aesten.wwrpg.items;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;

public class ItemRegistry {
    public static Item skeletonPunisher = Item.create(Material.STICK, 1)
            .addName("§6Skeleton Punisher")
            .addLore("§9A stick to break 'em bones!")
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
            .build("skeleton_punisher");

    public static Item exquisiteMeat = Item.create(Material.COOKED_BEEF, 5)
            .addName("§6Exquisite Meat")
            .addLore("§9Your only food source")
            .build("exquisite_meat");

    public static Item huntersBow = Item.create(Material.BOW, 1)
            .addName("§Hunter's Bow")
            .addLore("§9A one-hit kill bow")
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addDamage(384)
            .build("hunters_bow");

    

}
