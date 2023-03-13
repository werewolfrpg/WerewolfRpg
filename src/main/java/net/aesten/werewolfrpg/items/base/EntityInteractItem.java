package net.aesten.werewolfrpg.items.base;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public interface EntityInteractItem {
    void onEntityInteract(PlayerInteractAtEntityEvent event);
}
