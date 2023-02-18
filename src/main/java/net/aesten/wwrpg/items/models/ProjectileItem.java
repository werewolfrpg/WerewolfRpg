package net.aesten.wwrpg.items.models;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileItem {
    void onProjectileHit(ProjectileHitEvent event);
}
