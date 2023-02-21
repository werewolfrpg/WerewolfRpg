package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.models.EntityInteractItem;
import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.WerewolfItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialVillagerSummonWand extends WerewolfItem implements EntityInteractItem {
    @Override
    public String getId() {
        return "special_villager_summon_wand";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.GOLD_INGOT, 1)
                .addName(ChatColor.AQUA + "Special Villager Summon Wand")
                .addLore(ChatColor.LIGHT_PURPLE + "Right click an armor stand to use")
                .addLore(ChatColor.LIGHT_PURPLE + "A special shop villager will be summoned")
                .addLore(ChatColor.LIGHT_PURPLE + "Right clicking a villager will delete it")
                .build();
    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.ARMOR_STAND) {
            WerewolfGame.getShopManager().summonSpecialShopVillager(entity.getLocation());
            entity.remove();
        }
        else if (entity.getType() == EntityType.VILLAGER) {
            entity.remove();
            event.setCancelled(true);
        }
    }
}