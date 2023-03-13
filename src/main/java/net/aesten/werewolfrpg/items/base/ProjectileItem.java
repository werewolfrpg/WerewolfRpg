package net.aesten.werewolfrpg.items.base;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileItem {
    void onProjectileHit(ProjectileHitEvent event);
}
