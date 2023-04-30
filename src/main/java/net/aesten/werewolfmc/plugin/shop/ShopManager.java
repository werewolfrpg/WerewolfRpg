package net.aesten.werewolfmc.plugin.shop;

import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.items.registry.PlayerItem;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.entity.Villager.Profession;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class ShopManager implements Configurable {
    private final List<ShopWerewolfItem> basicShopItems;
    private final List<ShopWerewolfItem> specialShopItems;
    private static final Comparator<ShopWerewolfItem> comparator = (o1, o2) -> {
        if (o1.equals(o2)) return 0;
        return o1.getShopSlot() > o2.getShopSlot() ? 1 : -1;
    };

    public ShopManager() {
        basicShopItems = PlayerItem.getRegistry().values().stream().filter(item -> item instanceof ShopWerewolfItem).map(ShopWerewolfItem.class::cast).filter(item -> item.getShopType() == ShopType.BASIC).sorted(comparator).toList();
        specialShopItems = PlayerItem.getRegistry().values().stream().filter(item -> item instanceof ShopWerewolfItem).map(ShopWerewolfItem.class::cast).filter(item -> item.getShopType() == ShopType.SPECIAL).sorted(comparator).toList();
    }

    public List<ShopWerewolfItem> getAllShopItems() {
        List<ShopWerewolfItem> list = new ArrayList<>(basicShopItems);
        list.addAll(specialShopItems);
        return list;
    }

    private static void setVillagerSetting(Villager villager) {
        villager.setVillagerLevel(5);
        villager.setSilent(true);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setAdult();
        villager.setInvulnerable(true);
    }

    private static MerchantRecipe createRecipe(ShopWerewolfItem item) {
        MerchantRecipe recipe = new MerchantRecipe(item.getItem(), 1000000);
        recipe.addIngredient(new ItemStack(Material.EMERALD, item.getCost().get()));
        return recipe;
    }

    public void summonBasicShopVillager(Location location) {
        Villager basicVillager = (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER);
        setVillagerSetting(basicVillager);
        basicVillager.setProfession(Profession.CARTOGRAPHER);
        basicVillager.setCustomName(ChatColor.AQUA + "Basic Shop");
        basicVillager.setRecipes(basicShopItems.stream().map(ShopManager::createRecipe).toList());
    }

    public void summonSpecialShopVillager(Location location) {
        Villager specialVillager = (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER);
        setVillagerSetting(specialVillager);
        specialVillager.setProfession(Profession.LIBRARIAN);
        specialVillager.setCustomName(ChatColor.LIGHT_PURPLE + "Special Shop");
        specialVillager.setRecipes(specialShopItems.stream().map(ShopManager::createRecipe).toList());
    }

    public void updatePrices(World world) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Villager villager) {
                if (villager.getProfession().equals(Profession.CARTOGRAPHER)) {
                    villager.setRecipes(basicShopItems.stream().map(ShopManager::createRecipe).toList());
                }
                else if (villager.getProfession().equals(Profession.LIBRARIAN)) {
                    villager.setRecipes(specialShopItems.stream().map(ShopManager::createRecipe).toList());
                }
            }
        }
    }

    @Override
    public String getName() {
        return "wwmc-shop-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return new ArrayList<>(PlayerItem.getRegistry().values().stream().filter(item -> item instanceof ShopWerewolfItem).map(item -> ((ShopWerewolfItem) item).getCost()).toList());
    }
}
