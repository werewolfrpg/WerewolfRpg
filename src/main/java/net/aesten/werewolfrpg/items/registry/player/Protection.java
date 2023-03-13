package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.items.base.InteractItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Protection extends ShopWerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "protection";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.ARMOR_STAND, 1)
                .addName(ChatColor.GOLD + "Protection")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Cancel axe or bow damage once")
                .addLore(ChatColor.GRAY + "Active during one night")
                .addLore(ChatColor.GRAY + "Non-stackable")
                .addLore(ChatColor.GRAY + "Villager-only item")
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 4;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 2;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Player user = event.getPlayer();
        if (!game.isNight()) {
            WerewolfUtil.sendPluginText(user, "You cannot use this item during day time", ChatColor.RED);
            return;
        }
        WerewolfPlayerData data = game.getDataMap().get(user.getUniqueId());
        if (data.hasAlreadyUsedProtection()) {
            WerewolfUtil.sendPluginText(user, "You have already used one protection this night", ChatColor.RED);
        }
        else if (data.getRole() == Role.VILLAGER || data.getRole() == Role.POSSESSED) {
            data.setHasActiveProtection(true);
            data.setHasAlreadyUsedProtection(true);
            user.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 5, false, false, false));
            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
            game.getMap().getWorld().playSound(user.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f,1);
            WerewolfUtil.sendPluginText(user, "Protection activated", ChatColor.GREEN);
            game.getTracker().getPlayerStats(user.getUniqueId()).addProtectionUsed(true);
        }
        else {
            data.setHasAlreadyUsedProtection(true);
            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
            game.getMap().getWorld().playSound(user.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f,1);
            WerewolfUtil.sendPluginText(user, "Protection will have no effect", ChatColor.RED);
            game.getTracker().getPlayerStats(user.getUniqueId()).addProtectionUsed(false);
        }
    }
}
