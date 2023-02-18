package net.aesten.wwrpg.items.models;

import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractItem {
    void onPlayerInteract(PlayerInteractEvent event);
}
