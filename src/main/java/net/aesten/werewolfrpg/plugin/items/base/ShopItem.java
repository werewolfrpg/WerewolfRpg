package net.aesten.werewolfrpg.plugin.items.base;

import net.aesten.werewolfrpg.plugin.shop.ShopType;
import net.azalealibrary.configuration.property.Property;

public interface ShopItem {
    Integer getDefaultCost();
    ShopType getShopType();
    Integer getShopSlot();
    Property<Integer> getCost();
}
