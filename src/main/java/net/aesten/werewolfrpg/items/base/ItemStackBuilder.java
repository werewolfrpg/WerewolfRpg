package net.aesten.werewolfrpg.items.base;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class ItemStackBuilder {
    private final ItemStack itemStack;
    private final ItemMeta meta;
    private final List<String> lore;

    private ItemStackBuilder(Material material, int amount) {
        itemStack = new ItemStack(material, amount);
        meta = itemStack.getItemMeta();
        lore = new ArrayList<>();
    }

    public static ItemStackBuilder builder(Material material, int amount) {
        return new ItemStackBuilder(material, amount);
    }

    public ItemStackBuilder addName(String displayName) {
        meta.setDisplayName(displayName);
        return this;
    }

    public ItemStackBuilder addLore(String line) {
        lore.add(line);
        return this;
    }

    public ItemStackBuilder addEnchantment(Enchantment enchantment, int level) {
        meta.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemStackBuilder addFlags(ItemFlag... itemFlag) {
        meta.addItemFlags(itemFlag);
        return this;
    }

    public ItemStackBuilder addAttributeModifier(Attribute attribute, AttributeModifier attributeModifier) {
        meta.addAttributeModifier(attribute, attributeModifier);
        return this;
    }

    public ItemStackBuilder addPotionEffect(PotionEffectType effectType, int duration, int amplifier, boolean ambient, boolean particles, boolean icon) {
        ((PotionMeta) meta).addCustomEffect(new PotionEffect(effectType, duration, amplifier, ambient, particles, icon), true);
        return this;
    }

    public ItemStackBuilder setPotionColor(Color color) {
        ((PotionMeta) meta).setColor(color);
        return this;
    }

    public ItemStackBuilder setUnbreakable() {
        meta.setUnbreakable(true);
        return this;
    }

    public ItemStackBuilder addDamage(int damage) {
        ((Damageable) meta).setDamage(damage);
        return this;
    }

    public ItemStack build() {
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
