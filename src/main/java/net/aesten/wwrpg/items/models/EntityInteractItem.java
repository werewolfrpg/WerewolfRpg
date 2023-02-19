package net.aesten.wwrpg.items.models;

import org.bukkit.event.player.PlayerInteractEntityEvent;

public interface EntityInteractItem {
    void onPlayerInteract(PlayerInteractEntityEvent event);
}
