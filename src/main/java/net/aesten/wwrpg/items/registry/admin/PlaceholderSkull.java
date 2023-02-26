package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.WerewolfItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PlaceholderSkull extends WerewolfItem {
    @Override
    public String getId() {
        return "placeholder_skull";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.SKELETON_SKULL, 1)
                .addName(ChatColor.AQUA + "Placeholder Skull")
                .addLore(ChatColor.LIGHT_PURPLE + "Place it down where you want player heads")
                .addLore(ChatColor.LIGHT_PURPLE + "The orientation of the skulls will be kept")
                .addLore(ChatColor.LIGHT_PURPLE + "You can register these by clicking with a Skull Wand")
                .build();
    }
}
