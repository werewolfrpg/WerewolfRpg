package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.base.InteractItem;
import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.WerewolfItem;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
                .addLore(ChatColor.LIGHT_PURPLE + "The spawn locations will be temporarily stored")
                .addLore(ChatColor.LIGHT_PURPLE + "Use '/ww skeleton add <map>' to confirm addition")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block != null) {
            Location loc = block.getLocation();
            WerewolfGame.getSkeletonManager().addSpawnFromBlock(loc);
            WerewolfUtil.sendPluginText(event.getPlayer(), "Added (" + loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ") to spawn candidate list");
        }
    }
}
