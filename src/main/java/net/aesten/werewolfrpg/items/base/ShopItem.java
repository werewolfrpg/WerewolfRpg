package net.aesten.werewolfrpg.items.base;

import net.aesten.werewolfrpg.shop.ShopType;
import net.azalealibrary.configuration.property.Property;

public interface ShopItem {
    Integer getDefaultCost();
    ShopType getShopType();
    Integer getShopSlot();
    Property<Integer> getCost();
}
