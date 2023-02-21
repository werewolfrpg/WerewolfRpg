package net.aesten.wwrpg.items.models;

import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface EntityInteractItem {
    void onEntityInteract(PlayerInteractEntityEvent event);
}
