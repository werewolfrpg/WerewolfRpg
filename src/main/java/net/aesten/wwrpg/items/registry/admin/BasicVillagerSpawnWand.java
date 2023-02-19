package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.models.EntityInteractItem;
import net.aesten.wwrpg.items.models.WerewolfItem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class BasicVillagerSpawnWand extends WerewolfItem implements EntityInteractItem {
    @Override
    public String getId() {
        return null;
    }

    @Override
    protected ItemStack getBaseItem() {
        return null;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        if (entity.getType() == EntityType.ARMOR_STAND || entity.getType() == EntityType.VILLAGER ) {
            WerewolfGame.getShopManager().summonBasicShopVillager(entity.getLocation());
            entity.remove();
        }
    }
}
