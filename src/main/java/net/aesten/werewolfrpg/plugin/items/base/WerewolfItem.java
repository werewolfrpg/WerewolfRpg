package net.aesten.werewolfrpg.plugin.items.base;

import org.bukkit.inventory.ItemStack;

public abstract class WerewolfItem {
    protected ItemStack itemStack;

    public abstract String getId();
    protected abstract ItemStack getBaseItem();

    public ItemStack getItem() {
        if (itemStack == null) {
            itemStack = getBaseItem();
        }
        return itemStack;
    }
}
