package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.items.base.InteractItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.shop.ShopType;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class AshOfTheDead extends ShopWerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "ash_of_the_dead";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.GUNPOWDER, 1)
                .addName(ChatColor.GOLD + "Ash of the Dead")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Identify dead and alive players")
                .addEnchantment(Enchantment.LUCK, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 3;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.SPECIAL;
    }

    @Override
    public Integer getShopSlot() {
        return 5;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Player user = event.getPlayer();
        Objects.requireNonNull(event.getItem()).setAmount(event.getItem().getAmount() - 1);
        game.getMap().getWorld().playSound(user.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.6f,1);
        WerewolfUtil.sendPluginText(user, "Ash of the dead:");
        for (Player player : game.getParticipants()) {
            if (game.getDataMap().get(player.getUniqueId()).isAlive()) {
                WerewolfUtil.sendPluginText(user, player.getName(), ChatColor.GREEN);
            }
            else {
                WerewolfUtil.sendPluginText(user, player.getName(), ChatColor.RED);
            }
        }

        game.getTracker().getPlayerStats(user.getUniqueId()).addAsheUsed();
    }
}
