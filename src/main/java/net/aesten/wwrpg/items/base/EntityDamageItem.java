package net.aesten.wwrpg.items.base;

import org.bukkit.event.entity.EntityDamageByEntityEvent;

public interface EntityDamageItem {
    void onEntityDamage(EntityDamageByEntityEvent event);
}
