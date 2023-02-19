package net.aesten.wwrpg.items.registry.admin;

import net.aesten.wwrpg.items.models.EntityInteractItem;
import net.aesten.wwrpg.items.models.WerewolfItem;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class SpecialVillagerSpawnWand extends WerewolfItem implements EntityInteractItem {
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

    }
}
