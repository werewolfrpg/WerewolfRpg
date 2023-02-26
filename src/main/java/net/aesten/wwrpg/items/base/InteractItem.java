package net.aesten.wwrpg.items.base;

import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractItem {
    void onPlayerInteract(PlayerInteractEvent event);
}
