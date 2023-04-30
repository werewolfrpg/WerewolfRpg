package net.aesten.werewolfmc.plugin.items.base;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.azalealibrary.configuration.property.AssignmentPolicy;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;
import org.bukkit.inventory.ItemStack;

public abstract class ShopWerewolfItem extends WerewolfItem implements ShopItem {
    protected ItemStack itemStack;
    private static final AssignmentPolicy<Integer> SHOP_COST = AssignmentPolicy.create(i -> i > 0 && i <= 64, "Item costs should be between 1 and 64");
    protected final Property<Integer> cost = Property.create(PropertyType.INTEGER, "shop." + getId(), this::getDefaultCost)
            .addPolicy(SHOP_COST)
            .onChange(cost -> WerewolfGame.getShopManager().updatePrices(WerewolfGame.getInstance().getMap().getWorld()))
            .done();


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
