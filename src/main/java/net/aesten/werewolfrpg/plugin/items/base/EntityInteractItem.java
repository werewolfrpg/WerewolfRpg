package net.aesten.werewolfrpg.plugin.items.base;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public interface EntityInteractItem {
    void onEntityInteract(PlayerInteractAtEntityEvent event);
}
