package com.aesten.wwrpg.items;

import com.aesten.wwrpg.wwrpg;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Item implements Listener {

    private final ItemStack itemStack;
    private final String id;
    private final Consumer<PlayerInteractEvent> onToggle;

    public Item(ItemStack itemStack, String id, Consumer<PlayerInteractEvent> onToggle) {
        this.itemStack = itemStack;
        this.id = id;
        this.onToggle = onToggle;
        Bukkit.getPluginManager().registerEvents(this, wwrpg.getPlugin());
    }

    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    public String getId() {
        return id;
    }

    @EventHandler
    public final void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.hasItem() && Objects.requireNonNull(Objects.requireNonNull(event.getItem()).getItemMeta()).getDisplayName().equals(Objects.requireNonNull(itemStack.getItemMeta()).getDisplayName())) {
            onToggle.accept(event);
        }
    }

    public static Builder create(Material material, int amount) {
        return new Builder(material, amount);
    }

    public static class Builder {

        private final ItemStack itemStack;
        private final ItemMeta meta;
        private final List<String> lore;
        private Consumer<PlayerInteractEvent> onToggle = event -> {};

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

        public Builder unbreakable() {
            meta.setUnbreakable(true);
            return this;
        }

        public Builder onToggle(Consumer<PlayerInteractEvent> onToggle) {
            this.onToggle = onToggle;
            return this;
        }

        public Builder addDamage(int damage) {
            ((Damageable) meta).setDamage(damage);
            return this;
        }

        public Item build(String id) {
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
            return new Item(itemStack, id, onToggle);
        }
    }
}