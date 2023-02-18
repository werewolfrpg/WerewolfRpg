package net.aesten.wwrpg.items.models;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageItem {
    void onEntityDamage(EntityDamageByEntityEvent event);
}
