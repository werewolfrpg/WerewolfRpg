package net.aesten.wwrpg.items.registry.player;

import com.comphenix.protocol.wrappers.Pair;
import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.WerewolfPlayerData;
import net.aesten.wwrpg.items.base.ItemStackBuilder;
import net.aesten.wwrpg.items.base.ProjectileItem;
import net.aesten.wwrpg.items.base.ShopWerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class SharpArrow extends ShopWerewolfItem implements ProjectileItem {
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
        if (event.getHitEntity() instanceof Player player && projectile.getShooter() instanceof Player shooter) {
            WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                event.setCancelled(true);
                game.getTracker().getPlayerStats(shooter.getUniqueId()).addArrowHits(false);
            }
            else if (game.isNight() && data.hasActiveProtection()) {
                event.setCancelled(true);
                data.setHasActiveProtection(false);
                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                WerewolfUtil.sendPluginText(player, "Protection was activated", ChatColor.GREEN);
                game.getTracker().getPlayerStats(shooter.getUniqueId()).addArrowHits(false);
                game.getTracker().getPlayerStats(player.getUniqueId()).addProtectionTriggered();
            }
            else {
                player.setHealth(0);
                game.getTracker().getPlayerStats(shooter.getUniqueId()).addArrowHits(true);
                game.getTracker().getPlayerStats(shooter.getUniqueId()).addKills();
                game.getTracker().getSpecificDeathCauses().put(player.getUniqueId(), new Pair<>("arrow_hit", shooter.getUniqueId()));
            }
        }
        projectile.remove();
    }
}
