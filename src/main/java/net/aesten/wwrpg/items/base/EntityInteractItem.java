package net.aesten.wwrpg.items.base;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public interface EntityInteractItem {
    void onEntityInteract(PlayerInteractAtEntityEvent event);
}
