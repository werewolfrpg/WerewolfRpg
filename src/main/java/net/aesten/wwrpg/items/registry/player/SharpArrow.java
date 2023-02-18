package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.WerewolfPlayerData;
import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.ProjectileItem;
import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SharpArrow extends WerewolfItem implements ProjectileItem {
    @Override
    public String getId() {
        return "sharp_arrow";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.ARROW, 1)
                .addName(ChatColor.GOLD + "Sharp Arrow")
                .addLore(ChatColor.BLUE + "One-time use arrow")
                .build();
    }

    @Override
    public Integer getDefaultCost() {
        return 2;
    }

    @Override
    public ShopType getShopType() {
        return ShopType.BASIC;
    }

    @Override
    public Integer getShopSlot() {
        return 3;
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Projectile projectile = event.getEntity();
        if (event.getHitEntity() instanceof Player player) {
            WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
            Player shooter = (Player) projectile.getShooter();
            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                event.setCancelled(true);
                //todo history: save event
            }
            else if (game.isNight() && data.hasActiveProtection()) {
                event.setCancelled(true);
                data.setHasActiveProtection(false);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                WerewolfUtil.sendPluginText(player, "Protection was activated", ChatColor.GREEN);
                //todo history: save event
            }
            else {
                player.setHealth(0);
                //todo history: save event
                //todo data: player death cause
            }
        }
        projectile.remove();
    }
}
