package net.aesten.werewolfmc.plugin.items.base;

import net.aesten.werewolfmc.plugin.shop.ShopType;
import net.azalealibrary.configuration.property.Property;

public interface ShopItem {
    Integer getDefaultCost();
    ShopType getShopType();
    Integer getShopSlot();
    Property<Integer> getCost();
}
