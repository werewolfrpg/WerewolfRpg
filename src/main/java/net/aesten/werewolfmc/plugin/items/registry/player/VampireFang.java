package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import net.aesten.werewolfmc.plugin.items.base.EntityDamageItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;

public class VampireFang extends WerewolfItem implements EntityDamageItem {
    @Override
    public String getId() {
        return "vampire_fang";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.ECHO_SHARD, 1)
                .addName(ChatColor.LIGHT_PURPLE + "Vampire Fang")
                .addLore(ChatColor.GREEN + "Hit a player to use")
                .addLore(ChatColor.BLUE + "Convert a player into a servant")
                .addLore(ChatColor.BLUE + "The servant appears as a vampire on divination")
                .addLore(ChatColor.BLUE + "If the vampire dies, so will the servant")
                .build();
    }

    @Override
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
            game.getMap().getWorld().playSound(damager.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 0.6f, 1);
            WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());

            if (data.getRole() != Role.VAMPIRE && data.getRole() != Role.SERVANT) {
                ItemStack item = damager.getInventory().getItemInMainHand();
                item.setAmount(item.getAmount() - 1);
                WerewolfGame.getTeamsManager().switchRole(target, Role.SERVANT);
                target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 1, true, true));
                WerewolfUtil.sendPluginText(damager, target.getName() + " was converted into a Servant", ChatColor.DARK_PURPLE);
                WerewolfUtil.sendPluginText(target, "You are now a Servant", ChatColor.DARK_PURPLE);
                event.setCancelled(true);
            } else {
                WerewolfUtil.sendErrorText(damager, "Target is already in your faction");
            }
        }
    }
}
