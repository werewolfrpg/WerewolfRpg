package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.WerewolfTeams;
import net.aesten.wwrpg.items.models.InteractItem;
import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class TraitorsGuide extends WerewolfItem implements InteractItem {
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
        String werewolf = WerewolfTeams.getTeam(Role.WEREWOLF).getEntries().stream().toList()
                .get(new Random().nextInt(game.getPool().getWerewolfNumber()));
        WerewolfUtil.sendPluginText(user, werewolf + " is a werewolf!", ChatColor.RED);
    }
}
