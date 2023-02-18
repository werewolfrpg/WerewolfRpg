package net.aesten.wwrpg.configurations;

import net.aesten.wwrpg.items.models.ShopItem;
import net.aesten.wwrpg.items.registry.ItemManager;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;

import java.util.ArrayList;
import java.util.List;

public class WerewolfConfig implements Configurable {
    private final Property<Integer> skeletonSpawnNumberPerPlayer = new Property<>(PropertyType.INTEGER, () -> 12, "skeleton.spawn_number_per_player", "number of spawning skeletons per player", false);
    private final Property<Double> skeletonDamage = new Property<>(PropertyType.DOUBLE, () -> 4.0, "skeleton.damage", "skeleton damage", false);
    private final Property<Double> skeletonHealth = new Property<>(PropertyType.DOUBLE, () -> 12.0, "skeleton.health", "skeleton health", false);

    @Override
    public String getName() {
        return "wwrpg-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        List<ConfigurableProperty<?, ?>> properties = new ArrayList<>(ItemManager.getRegistry().values().stream().map(ShopItem::getCost).toList());
        properties.add(skeletonSpawnNumberPerPlayer);
        properties.add(skeletonDamage);
        properties.add(skeletonHealth);
        return properties;
    }

    public int getSkeletonSpawnNumberPerPlayer() {
        return skeletonSpawnNumberPerPlayer.get();
    }

    public double getSkeletonDamage() {
        return skeletonDamage.get();
    }

    public double getSkeletonHealth() {
        return skeletonHealth.get();
    }
}
