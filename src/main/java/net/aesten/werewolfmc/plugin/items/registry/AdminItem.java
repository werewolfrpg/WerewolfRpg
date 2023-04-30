package net.aesten.werewolfmc.plugin.items.registry;

import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.items.registry.admin.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum AdminItem {
    BASIC_VILLAGER_SUMMON_WAND(new BasicVillagerSummonWand()),
    SPECIAL_VILLAGER_SUMMON_WAND(new SpecialVillagerSummonWand()),
    SKELETON_SPAWN_POINT_WAND(new SkeletonSpawnPointWand()),
    PLACEHOLDER_SKULL(new PlaceholderSkull()),
    SKULL_WAND(new SkullWand());

    private static final Map<String, WerewolfItem> registry = new HashMap<>();

    static {
        for (AdminItem item : values()) {
            registry.put(item.id, item.werewolfItem);
        }
    }

    public static Map<String, WerewolfItem> getRegistry() {
        return registry;
    }

    public final String id;
    public final WerewolfItem werewolfItem;

    AdminItem(WerewolfItem werewolfItem) {
        this.id = werewolfItem.getId();
        this.werewolfItem = werewolfItem;
    }

    public ItemStack getItem() {
        return werewolfItem.getItem();
    }
}
