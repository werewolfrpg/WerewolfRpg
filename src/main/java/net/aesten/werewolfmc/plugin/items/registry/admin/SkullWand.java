package net.aesten.werewolfmc.plugin.items.registry.admin;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.map.MapEditingHelper;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

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
                .addLore(ChatColor.YELLOW + "Use '/ww map select <map>' to select the map you are editing")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.SKELETON_SKULL) {
            MapEditingHelper helper = WerewolfGame.getMapManager().getHelper();
            if (helper.hasSelectedMap(event.getPlayer())) {
                WerewolfMap map = helper.getSelectedMap(event.getPlayer());
                Vector vector = event.getClickedBlock().getLocation().toVector();
                if (helper.addOrElseRemove(map.getSkullLocations(), vector)) {
                    WerewolfUtil.sendPluginText(event.getPlayer(), "Added (" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ") to skulls");
                }
                else {
                    WerewolfUtil.sendPluginText(event.getPlayer(), "Removed (" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ") from skulls");
                }
            }
            else {
                WerewolfUtil.sendPluginText(event.getPlayer(), "You have not selected any map", ChatColor.RED);
                WerewolfUtil.sendPluginText(event.getPlayer(), "Use command: /ww map select <map>", ChatColor.RED);
            }
        }
    }
}
