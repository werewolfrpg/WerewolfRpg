package net.aesten.werewolfrpg.items.registry.admin;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.map.MapEditingHelper;
import net.aesten.werewolfrpg.items.base.InteractItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.WerewolfItem;
import net.aesten.werewolfrpg.map.WerewolfMap;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class SkeletonSpawnPointWand extends WerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "skeleton_spawn_point_wand";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.BONE, 1)
                .addName(ChatColor.AQUA + "Skeleton Spawn Point Wand")
                .addLore(ChatColor.LIGHT_PURPLE + "Click a block to select a spawn point")
                .addLore(ChatColor.LIGHT_PURPLE + "Saves the spawn location")
                .addLore(ChatColor.LIGHT_PURPLE + "Deletes the spawn location if already registered")
                .addLore(ChatColor.YELLOW + "Use '/ww map select <map>' to select the map you are editing")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        MapEditingHelper helper = WerewolfGame.getMapManager().getHelper();
        if (helper.hasSelectedMap(event.getPlayer())) {
            WerewolfMap map = helper.getSelectedMap(event.getPlayer());
            Block block = event.getClickedBlock();
            if (block != null) {
                Vector vector = WerewolfUtil.getVectorCenterSpawn(block.getLocation());
                if (helper.addOrElseRemove(map.getSkeletonSpawnLocations(), vector)) {
                    WerewolfUtil.sendPluginText(event.getPlayer(), "Added (" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ") to spawns");
                }
                else {
                    WerewolfUtil.sendPluginText(event.getPlayer(), "Removed (" + vector.getX() + ", " + vector.getY() + ", " + vector.getZ() + ") from spawns");
                }
            }
        }
        else {
            WerewolfUtil.sendPluginText(event.getPlayer(), "You have not selected any map", ChatColor.RED);
            WerewolfUtil.sendPluginText(event.getPlayer(), "Use command: /ww map select <map>", ChatColor.RED);
        }
    }
}
