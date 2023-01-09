package com.aesten.wwrpg.items;

import com.aesten.wwrpg.wwrpg;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
//import org.bukkit.attribute.Attribute;
//import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Event;
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
    private final Consumer<Event> onEventCall;

    public WerewolfItem(ItemStack itemStack, String id, int defaultCost, Consumer<Event> onEventCall) {
        this.itemStack = itemStack;
        this.id = id;
        this.defaultCost = defaultCost;
        this.onEventCall = onEventCall;
        Bukkit.getPluginManager().registerEvents(this, wwrpg.getPlugin());
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

    @EventHandler
    public final void onEventCall(Event event) {
        if (event instanceof PlayerInteractEvent || event instanceof PlayerMoveEvent ||
                event instanceof ProjectileHitEvent || event instanceof EntityDamageByEntityEvent) {
            onEventCall.accept(event);
        }
    }

    public static Builder create(Material material, int amount) {
        return new Builder(material, amount);
    }

    public static class Builder {

        private final ItemStack itemStack;
        private final ItemMeta meta;
        private final List<String> lore;
        private int defaultCost;
        private Consumer<Event> onEventCall = event -> {};

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

//        public Builder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
//            meta.addAttributeModifier(attribute, attributeModifier);
//            return this;
//        }

        public Builder addPotionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
            ((PotionMeta) meta).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, particles, icon), true);
            return this;
        }

        public Builder setPotionColor(Color color) {
            ((PotionMeta) meta).setColor(color);
            return this;
        }

//        public Builder unbreakable() {
//            meta.setUnbreakable(true);
//            return this;
//        }

        public Builder onEventCall(Consumer<Event> onEventCall) {
            this.onEventCall = onEventCall;
            return this;
        }

        public Builder setCost(int defaultCost) {
            this.defaultCost = defaultCost;
            return this;
        }

        public Builder addDamage(int damage) {
            ((Damageable) meta).setDamage(damage);
            return this;
        }

        public WerewolfItem build(String id) {
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            return new WerewolfItem(itemStack, id, defaultCost, onEventCall);
        }
    }
}