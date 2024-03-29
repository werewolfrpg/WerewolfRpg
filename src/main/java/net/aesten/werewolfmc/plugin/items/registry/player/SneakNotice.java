package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.shop.ShopType;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public class SneakNotice extends ShopWerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "sneak_notice";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.GLOBE_BANNER_PATTERN, 1)
                .addName(ChatColor.GOLD + "Sneak Notice")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Notifies when divination is used on you")
                .addLore(ChatColor.GRAY + "Active during one night")
                .addEnchantment(Enchantment.LUCK, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 1;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 7;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Player user = event.getPlayer();
        if (!game.isNight()) {
            WerewolfUtil.sendPluginText(user, "This item can only be used during night time", ChatColor.RED);
        }
        else if (game.getDataMap().get(user.getUniqueId()).hasActiveSneakNotice()) {
            WerewolfUtil.sendPluginText(user, "You have already used this item this night", ChatColor.RED);
        }
        else {
            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
            game.getDataMap().get(user.getUniqueId()).setHasActiveSneakNotice(true);
            game.getMap().getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CONVERTED, 0.8f,1);
            WerewolfUtil.sendPluginText(user, "You will be notified during this night when divinated", ChatColor.GREEN);
            game.getTracker().getPlayerStats(user.getUniqueId()).addSneakNoticeUsed();
        }
    }
}
