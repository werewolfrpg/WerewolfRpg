package net.aesten.werewolfrpg.plugin.items.registry.player;

import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.aesten.werewolfrpg.plugin.data.Role;
import net.aesten.werewolfrpg.plugin.items.base.InteractItem;
import net.aesten.werewolfrpg.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfrpg.plugin.shop.ShopType;
import net.aesten.werewolfrpg.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TraitorsGuide extends ShopWerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "traitors_guide";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.BOOK, 1)
                .addName(ChatColor.DARK_RED + "Traitor's Guide")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.DARK_PURPLE + "Get the name of one Werewolf")
                .addLore(ChatColor.GRAY + "Same name can be given many times")
                .addLore(ChatColor.GRAY + "Only the Traitor can use this item")
                .addEnchantment(Enchantment.LUCK, 1)
                .addFlags(ItemFlag.HIDE_ENCHANTS)
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
        return 8;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Player user = event.getPlayer();
        user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
        game.getMap().getWorld().playSound(user.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 0.8f,1);
        String werewolf = WerewolfGame.getTeamsManager().getFaction(Role.WEREWOLF).getTeam().getEntries().stream().toList()
                .get(new Random().nextInt(game.getPool().getWerewolfNumber()));
        WerewolfUtil.sendPluginText(user, werewolf + " is a werewolf!", ChatColor.RED);

        WerewolfGame.getInstance().getTracker().getPlayerStats(user.getUniqueId()).addTraitorsGuideUsed();
    }
}
