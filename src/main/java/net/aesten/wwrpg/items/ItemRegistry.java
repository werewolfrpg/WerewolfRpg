package net.aesten.wwrpg.items;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.potion.PotionEffectType;

public class ItemRegistry {
    public static final WerewolfItem SKELETON_PUNISHER = WerewolfItem.create(Material.STICK, 1)
            .addName("§6Skeleton Punisher")
            .addLore("§9A stick to break 'em bones!")
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
            .build("skeleton_punisher");

    public static final WerewolfItem EXQUISITE_MEAT = WerewolfItem.create(Material.COOKED_BEEF, 5)
            .addName("§6Exquisite Meat")
            .addLore("§9Your only food source")
            .setCost(1)
            .build("exquisite_meat");

    public static final WerewolfItem HUNTERS_BOW = WerewolfItem.create(Material.BOW, 1)
            .addName("§6Hunter's Bow")
            .addLore("§9A one-hit kill bow")
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addDamage(384)
            .setCost(2)
            .build("hunters_bow");
    
    public static final WerewolfItem SHARP_ARROW = WerewolfItem.create(Material.ARROW, 1)
            .addName("§6Sharp Arrow")
            .addLore("§9One-time use arrow")
            .setCost(2)
            .onEventCall((event) -> {
                //if game not running skip
                if (event instanceof ProjectileHitEvent projectileHitEvent) {
                    if (projectileHitEvent.getEntity() instanceof Arrow arrow) {
                        arrow.remove();
                        if (projectileHitEvent.getHitEntity() instanceof Player player) {
                            if (NIGHT) {
                                if (PLAYER IS VAMPIRE) {
                                    projectileHitEvent.setCancelled(true);
                                }
                                else if (PROTECTION) {
                                    projectileHitEvent.setCancelled(true);
                                    //remove protection
                                    //send message protection activated
                                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                }
                            }
                        }
                    }
                }
            })
            .build("sharp_arrow");

    public static final WerewolfItem STUN_GRENADE = WerewolfItem.create(Material.SNOWBALL, 1)
            .addName("§6Stun Grenade")
            .addLore("§9Stuns target for 5 seconds")
            .addLore("§7Only on direct hit")
            .setCost(2)
            .build("stun_grenade");

    public static final WerewolfItem WEREWOLF_AXE = WerewolfItem.create(Material.NETHERITE_AXE, 1)
            .addName("§4Werewolf Axe")
            .addLore("§5The murderous axe of werewolves")
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addDamage(2031)
            .setCost(3)
            .build("werewolf_axe");

    public static final WerewolfItem SKELETON_SLICER = WerewolfItem.create(Material.WOODEN_SWORD, 1)
            .addName("§6Skeleton Slicer")
            .addLore("§9Kills skeletons faster")
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 5)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .addDamage(19)
            .setCost(4)
            .build("skeleton_slicer");

    public static final WerewolfItem INVISIBILITY_POTION = WerewolfItem.create(Material.POTION, 1)
            .addName("§6Invisibility Potion")
            .addLore("§9Become invisible")
            .addLore("§7Active for 30 seconds")
            .addPotionEffect(PotionEffectType.INVISIBILITY, 600, 0, false, false, true)
            .setPotionColor(Color.AQUA)
            .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(4)
            .build("invisibility_potion");

    public static final WerewolfItem SWIFTNESS_POTION = WerewolfItem.create(Material.POTION, 1)
            .addName("§6Swiftness Potion")
            .addLore("§9Eternal minor speed boost")
            .addPotionEffect(PotionEffectType.SPEED, 72000, 0, false, false, false)
            .setPotionColor(Color.GRAY)
            .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(2)
            .build("swiftness_potion");

    public static final WerewolfItem LIGHT_OF_REVELATION = WerewolfItem.create(Material.SUNFLOWER, 1)
            .addName("§6Light Of Revelation")
            .addLore("§aRight click to use")
            .addLore("§9Light up all players")
            .addLore("§7Active for 30 seconds")
            .setCost(3)
            .build("light_of_revelation");

    public static final WerewolfItem PROTECTION = WerewolfItem.create(Material.ARMOR_STAND, 1)
            .addName("§6Protection")
            .addLore("§aRight click to use")
            .addLore("§9Cancel axe or bow damage once")
            .addLore("§7Active during one night")
            .addLore("§7Non-stackable")
            .addLore("§7Villager-only item")
            .setCost(4)
            .build("protection");

    public static final WerewolfItem DIVINATION = WerewolfItem.create(Material.HEART_OF_THE_SEA, 1)
            .addName("§6Divination")
            .addLore("§aThe item will disappear from inventory")
            .addLore("§aIt will add 1 to your divination counter")
            .addLore("§9Unveil the identity of a player")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(6)
            .build("divination");

    public static final WerewolfItem TRAITORS_GUIDE = WerewolfItem.create(Material.BOOK, 1)
            .addName("§4Traitor's Guide")
            .addLore("§aRight click to use")
            .addLore("§5Get the name of one werewolf")
            .addLore("§7Same name can be given many times")
            .addLore("§7Traitor-only item")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(4)
            .build("traitors_guide");

    public static final WerewolfItem HOLY_STAR = WerewolfItem.create(Material.NETHER_STAR, 1)
            .addName("§6Holy Star")
            .addLore("§aHit a player to use")
            .addLore("§9Can kill a vampire during night time")
            .addLore("§7When used on non-vampire item is lost")
            .setCost(2)
            .build("holy_star");

    public static final WerewolfItem SNEAK_NOTICE = WerewolfItem.create(Material.GLOBE_BANNER_PATTERN, 1)
            .addName("§6Sneak Notice")
            .addLore("§aRight click to use")
            .addLore("§9Warning when divination is used on you")
            .addLore("§7Active during one night")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(2)
            .build("sneak_notice");

    public static final WerewolfItem ASH_OF_THE_DEAD = WerewolfItem.create(Material.GUNPOWDER, 1)
            .addName("§6Ash of the Dead")
            .addLore("§aRight click to use")
            .addLore("§9Gives you list of dead players")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(3)
            .build("ash_of_the_dead");

    public static final WerewolfItem CURSE_SPEAR = WerewolfItem.create(Material.TRIDENT, 1)
            .addName("§6Curse Spear")
            .addLore("§aHit enemy to use")
            .addLore("§9First hit on a player curses him")
            .addLore("§9Second hit kills him instantly")
            .addLore("§7Second hit effect negated by Protection")
            .addLore("§7Breaks on usage")
            .addDamage(250)
            .setCost(3)
            .build("curse_spear");
}
