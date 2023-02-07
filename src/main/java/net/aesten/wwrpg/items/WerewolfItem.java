package net.aesten.wwrpg.items;

import net.aesten.wwrpg.WerewolfRpg;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class WerewolfItem implements Listener {

    private final ItemStack itemStack;
    private final String id;
    private final int defaultCost;
    private final String shopType; //todo shop type
    private final int shopSlot; //todo shop slot
    private final Consumer<PlayerInteractEvent> onPlayerInteract;
    private final Consumer<EntityDamageByEntityEvent> onEntityDamageEntity;
    private final Consumer<ProjectileHitEvent> onProjectileHit;
    private final Consumer<PlayerMoveEvent> onPlayerMove;

    public WerewolfItem(ItemStack itemStack,
                        String id,
                        int defaultCost,
                        String shopType,
                        int shopSlot,
                        Consumer<PlayerInteractEvent> onPlayerInteract,
                        Consumer<EntityDamageByEntityEvent> onEntityDamageEntity,
                        Consumer<ProjectileHitEvent> onProjectileHit,
                        Consumer<PlayerMoveEvent> onPlayerMove) {
        this.itemStack = itemStack;
        this.id = id;
        this.defaultCost = defaultCost;
        this.shopType = shopType;
        this.shopSlot = shopSlot;
        this.onPlayerInteract = onPlayerInteract;
        this.onEntityDamageEntity = onEntityDamageEntity;
        this.onProjectileHit = onProjectileHit;
        this.onPlayerMove = onPlayerMove;
        Bukkit.getPluginManager().registerEvents(this, WerewolfRpg.getPlugin());
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public String getId() {
        return id;
    }

    public int getDefaultCost() {
        return defaultCost;
    }

    public String getShopType() {
        return shopType;
    }

    public int getShopSlot() {
        return shopSlot;
    }

    @EventHandler
    public final void onPlayerInteract(PlayerInteractEvent event) {
        onPlayerInteract.accept(event);
    }

    @EventHandler
    public final void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        onEntityDamageEntity.accept(event);
    }

    @EventHandler
    public final void onProjectileHit(ProjectileHitEvent event) {
        onProjectileHit.accept(event);
    }

    @EventHandler
    public final void onPlayerMove(PlayerMoveEvent event) {
        onPlayerMove.accept(event);
    }

    public static Builder create(Material material, int amount) {
        return new Builder(material, amount);
    }

    public static class Builder {

        private final ItemStack itemStack;
        private final ItemMeta meta;
        private final List<String> lore;
        private int defaultCost;
        private String shopType;
        private int shopSlot;
        private Consumer<PlayerInteractEvent> onPlayerInteract = event -> {};
        private Consumer<EntityDamageByEntityEvent> onEntityDamageEntity = event -> {};
        private Consumer<ProjectileHitEvent> onProjectileHit = event -> {};
        private Consumer<PlayerMoveEvent> onPlayerMove = event -> {};

        private Builder(Material material, int amount) {
            itemStack = new ItemStack(material, amount);
            meta = itemStack.getItemMeta();
            lore = new ArrayList<>();
        }

        public Builder addName(String displayName) {
            meta.setDisplayName(displayName);
            return this;
        }

        public Builder addLore(String line) {
            lore.add(line);
            return this;
        }

        public Builder addEnchantment(Enchantment enchantment, int level) {
            meta.addEnchant(enchantment, level, true);
            return this;
        }

        public Builder addFlags(ItemFlag... itemFlag) {
            meta.addItemFlags(itemFlag);
            return this;
        }

        public Builder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
            meta.addAttributeModifier(attribute, attributeModifier);
            return this;
        }

        public Builder addPotionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
            ((PotionMeta) meta).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, particles, icon), true);
            return this;
        }

        public Builder setPotionColor(Color color) {
            ((PotionMeta) meta).setColor(color);
            return this;
        }

        public Builder setUnbreakable() {
            meta.setUnbreakable(true);
            return this;
        }

        public Builder onPlayerInteract(Consumer<PlayerInteractEvent> onPlayerInteract) {
            this.onPlayerInteract = onPlayerInteract;
            return this;
        }

        public Builder onEntityDamageEntity(Consumer<EntityDamageByEntityEvent> onEntityDamageEntity) {
            this.onEntityDamageEntity = onEntityDamageEntity;
            return this;
        }

        public Builder onProjectileHit(Consumer<ProjectileHitEvent> onProjectileHit) {
            this.onProjectileHit = onProjectileHit;
            return this;
        }

        public Builder onPlayerMove(Consumer<PlayerMoveEvent> onPlayerMove) {
            this.onPlayerMove = onPlayerMove;
            return this;
        }

        public Builder setCost(int defaultCost) {
            this.defaultCost = defaultCost;
            return this;
        }

        public Builder setShopType(String shopType) {
            this.shopType = shopType;
            return this;
        }

        public Builder setShopSlot(int shopSlot) {
            this.shopSlot= shopSlot;
            return this;
        }

        public Builder addDamage(int damage) {
            ((Damageable) meta).setDamage(damage);
            return this;
        }

        public WerewolfItem build(String id) {
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            return new WerewolfItem(itemStack, id, defaultCost, shopType, shopSlot, onPlayerInteract, onEntityDamageEntity, onProjectileHit, onPlayerMove);
        }
    }
}