package net.aesten.wwrpg.items.base;

import net.aesten.wwrpg.shop.ShopType;
import net.azalealibrary.configuration.property.Property;

public interface ShopItem {
    Integer getDefaultCost();
    ShopType getShopType();
    Integer getShopSlot();
    Property<Integer> getCost();
}
