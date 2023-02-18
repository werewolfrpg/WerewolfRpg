package net.aesten.wwrpg.items.registry.player;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.items.models.ItemStackBuilder;
import net.aesten.wwrpg.items.models.PlayerMoveItem;
import net.aesten.wwrpg.items.models.ProjectileItem;
import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.shop.ShopType;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class StunGrenade extends WerewolfItem implements ProjectileItem, PlayerMoveItem {
    @Override
    public String getId() {
        return "stun_grenade";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.SNOWBALL, 1)
                .addName(ChatColor.GOLD + "Stun Grenade")
                .addLore(ChatColor.BLUE + "Stuns target for 5 seconds")
                .addLore(ChatColor.GRAY + "Effective radius of 2 blocks")
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
        return 4;
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        Location landingPosition;
        if (event.getHitEntity() instanceof Player player) {
            landingPosition = player.getLocation();
        }
        else if (event.getHitBlock() != null) {
            landingPosition = event.getHitBlock().getLocation();
        }
        else {
            return;
        }

        World world = game.getMap().getWorld();
        world.playSound(landingPosition, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 1,1);
        world.spawnParticle(Particle.FIREWORKS_SPARK, landingPosition, 300);

        for (Entity entity : world.getNearbyEntities(landingPosition, 2, 2, 2)) {
            if (entity instanceof Player player && game.isParticipant(player)) {
                game.getDataMap().get(player.getUniqueId()).setStunned(true);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100, 0,false, false, false));
                WerewolfUtil.runDelayedTask(100, () -> {
                    game.getDataMap().get(player.getUniqueId()).setStunned(false);
                });
            }
        }
    }

    @Override
    public void onPlayerMove(PlayerMoveEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        if (game.isParticipant(event.getPlayer()) && game.getDataMap().get(event.getPlayer().getUniqueId()).isStunned()) {
            event.setTo(event.getFrom());
        }
    }
}
