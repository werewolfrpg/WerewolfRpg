package net.aesten.werewolfrpg.plugin.items.base;

import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractItem {
    void onPlayerInteract(PlayerInteractEvent event);
}
