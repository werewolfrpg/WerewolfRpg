package net.aesten.werewolfrpg.plugin.items.base;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileItem {
    void onProjectileHit(ProjectileHitEvent event);
}
