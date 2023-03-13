package net.aesten.werewolfrpg.items.base;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageItem {
    void onEntityDamage(EntityDamageByEntityEvent event);
}
