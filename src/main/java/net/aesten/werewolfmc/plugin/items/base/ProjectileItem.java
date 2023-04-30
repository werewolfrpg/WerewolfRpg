package net.aesten.werewolfmc.plugin.items.base;

import org.bukkit.event.entity.ProjectileHitEvent;

public interface ProjectileItem {
    void onProjectileHit(ProjectileHitEvent event);
}
