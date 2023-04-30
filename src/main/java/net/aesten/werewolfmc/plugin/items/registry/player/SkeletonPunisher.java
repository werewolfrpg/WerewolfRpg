package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SkeletonPunisher extends WerewolfItem {

    @Override
    public String getId() {
        return "skeleton_punisher";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.STICK, 1)
                .addName(ChatColor.GOLD + "Skeleton Punisher")
                .addLore(ChatColor.BLUE + "A stick to break 'em bones!")
                .addEnchantment(Enchantment.DAMAGE_UNDEAD, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
                .build();
    }
}
