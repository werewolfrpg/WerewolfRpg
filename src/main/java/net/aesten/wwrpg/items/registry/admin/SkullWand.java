package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.items.base.InteractItem;
import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.WerewolfItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SkullWand extends WerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "skull_wand";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.BLAZE_ROD, 1)
                .addName(ChatColor.AQUA + "Skull Wand")
                .addLore(ChatColor.LIGHT_PURPLE + "Right-click a Placeholder Skull to use")
                .addLore(ChatColor.LIGHT_PURPLE + "Saves the skull's location")
                .addLore(ChatColor.LIGHT_PURPLE + "Deletes the skull's location if already registered")
                .addLore(ChatColor.LIGHT_PURPLE + "Use command '/ww skull clear <map>' to reset saved locations")
                .addLore(ChatColor.LIGHT_PURPLE + "Use command '/ww skull list <map>' to list the locations")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SKELETON_SKULL) {
            
        }
    }
}
