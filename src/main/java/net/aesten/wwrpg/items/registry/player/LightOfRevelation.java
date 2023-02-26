package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.base.InteractItem;
import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.ShopWerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Objects;

public class LightOfRevelation extends ShopWerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "light_of_revelation";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.SUNFLOWER, 1)
                .addName(ChatColor.GOLD + "Light Of Revelation")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Applies glowing to all players except user")
                .addLore(ChatColor.GRAY + "Active for 30 seconds")
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
        return 1;
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Player user = event.getPlayer();
        for (Player player : game.getParticipants()) {
            if (!player.equals(user)) {
                if (game.getDataMap().get(player.getUniqueId()).isAlive()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, false, false, false));
                }
            }
        }
        Objects.requireNonNull(event.getItem()).setAmount(event.getItem().getAmount() - 1);
        game.getMap().getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.6f,1);
        WerewolfUtil.sendPluginText(user, "Players are revealed");

        game.getTracker().getPlayerStats(user.getUniqueId()).addRevelationUsed();
    }
}
