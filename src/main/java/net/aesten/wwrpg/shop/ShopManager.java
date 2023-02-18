package net.aesten.wwrpg.shop;

import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.items.registry.ItemManager;
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
import java.util.List;
import java.util.Objects;

public class ShopManager {
    private static final List<WerewolfItem> basicShopItems = new ArrayList<>();
    private static final List<WerewolfItem> specialShopItems = new ArrayList<>();

    public static void initShopLists() {
        basicShopItems.addAll(ItemManager.getRegistry().values().stream().filter(item -> item.getShopType() == ShopType.BASIC).toList());
        specialShopItems.addAll(ItemManager.getRegistry().values().stream().filter(item -> item.getShopType() == ShopType.SPECIAL).toList());
    }

    private static void setVillagerSetting(Villager villager) {
        villager.setVillagerLevel(5);
        villager.setSilent(true);
        villager.setCustomNameVisible(true);
        villager.setAI(false);
        villager.setAdult();
        villager.setInvulnerable(true);
    }

    private static MerchantRecipe createRecipe(WerewolfItem werewolfItem) {
        MerchantRecipe recipe = new MerchantRecipe(werewolfItem.getItem(), 1000000);
        recipe.addIngredient(new ItemStack(Material.EMERALD, werewolfItem.getCost().get()));
        return recipe;
    }

    public static void summonBasicShopVillager(Location location) {
        Villager basicVillager = (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER);
        setVillagerSetting(basicVillager);
        basicVillager.setProfession(Profession.CARTOGRAPHER);
        basicVillager.setCustomName(ChatColor.AQUA + "Basic Shop");
        basicVillager.setRecipes(basicShopItems.stream().map(ShopManager::createRecipe).toList());
    }

    public static void summonSpecialShopVillager(Location location) {
        Villager specialVillager = (Villager) Objects.requireNonNull(location.getWorld()).spawnEntity(location, EntityType.VILLAGER);
        setVillagerSetting(specialVillager);
        specialVillager.setProfession(Profession.LIBRARIAN);
        specialVillager.setCustomName(ChatColor.LIGHT_PURPLE + "Special Shop");
        specialVillager.setRecipes(specialShopItems.stream().map(ShopManager::createRecipe).toList());
    }

    public static void updatePrices(World world) {
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
}
