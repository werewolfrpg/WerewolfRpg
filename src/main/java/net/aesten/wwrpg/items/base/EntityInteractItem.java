package net.aesten.wwrpg.items.base;

import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface EntityInteractItem {
    void onEntityInteract(PlayerInteractEntityEvent event);
}