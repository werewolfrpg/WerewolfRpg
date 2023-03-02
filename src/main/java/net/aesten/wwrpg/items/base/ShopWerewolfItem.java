package net.aesten.wwrpg.items.base;

import net.azalealibrary.configuration.property.AssignmentPolicy;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;
import org.bukkit.inventory.ItemStack;

public abstract class ShopWerewolfItem extends WerewolfItem implements ShopItem {
    protected ItemStack itemStack;
    private static final AssignmentPolicy<Integer> SHOP_COST = AssignmentPolicy.create(i -> i > 0 && i <= 64, "Item costs should be between 1 and 64");
    protected final Property<Integer> cost = new Property<>(PropertyType.INTEGER, this::getDefaultCost, "shop." + getId(), "change the cost of the item", false, SHOP_COST);

    public abstract String getId();
    protected abstract ItemStack getBaseItem();

    public ItemStack getItem() {
        if (itemStack == null) {
            itemStack = getBaseItem();
        }
        return itemStack;
    }

    @Override
    public final Property<Integer> getCost() {
        return cost;
    }
}
