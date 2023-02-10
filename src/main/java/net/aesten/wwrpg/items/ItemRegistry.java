package net.aesten.wwrpg.items;

import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.data.WerewolfPlayerData;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;

public class ItemRegistry {
    private static final HashMap<String, WerewolfItem> registry = new HashMap<>();

    public static HashMap<String, WerewolfItem> getRegistry() {
        return registry;
    }
    public static final WerewolfItem SKELETON_PUNISHER = WerewolfItem.create(Material.STICK, 1)
            .addName(ChatColor.GOLD + "Skeleton Punisher")
            .addLore(ChatColor.BLUE + "A stick to break 'em bones!")
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES)
            .build("skeleton_punisher");

    public static final WerewolfItem EXQUISITE_MEAT = WerewolfItem.create(Material.COOKED_BEEF, 5)
            .addName(ChatColor.GOLD + "Exquisite Meat")
            .addLore(ChatColor.BLUE + "Your only food source")
            .setCost(1)
            .setShopType("basic")
            .setShopSlot(0)
            .build("exquisite_meat");

    public static final WerewolfItem HUNTERS_BOW = WerewolfItem.create(Material.BOW, 1)
            .addName(ChatColor.GOLD + "Hunter's Bow")
            .addLore(ChatColor.BLUE + "A one-shot bow")
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addDamage(384)
            .setCost(2)
            .setShopType("basic")
            .setShopSlot(2)
            .build("hunters_bow");
    
    public static final WerewolfItem SHARP_ARROW = WerewolfItem.create(Material.ARROW, 1)
            .addName(ChatColor.GOLD + "Sharp Arrow")
            .addLore(ChatColor.BLUE + "One-time use arrow")
            .setCost(2)
            .setShopType("basic")
            .setShopSlot(3)
            .onProjectileHit(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                Projectile projectile = event.getEntity();
                if ((projectile instanceof Arrow)) {
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
            })
            .build("sharp_arrow");

    public static final WerewolfItem STUN_GRENADE = WerewolfItem.create(Material.SNOWBALL, 1)
            .addName(ChatColor.GOLD + "Stun Grenade")
            .addLore(ChatColor.BLUE + "Stuns target for 5 seconds")
            .addLore(ChatColor.GRAY + "Effective radius of 2 blocks")
            .setCost(2)
            .setShopType("basic")
            .setShopSlot(4)
            .onProjectileHit(event -> { //todo db
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                Projectile projectile = event.getEntity();
                Player shooter = (Player) projectile.getShooter();
                if (projectile instanceof Snowball) {
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
            })
            .onPlayerMove(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if (game.isParticipant(event.getPlayer()) && game.getDataMap().get(event.getPlayer().getUniqueId()).isStunned()) {
                    event.setTo(event.getFrom());
                }
            })
            .build("stun_grenade");

    public static final WerewolfItem WEREWOLF_AXE = WerewolfItem.create(Material.NETHERITE_AXE, 1)
            .addName(ChatColor.DARK_RED + "Werewolf Axe")
            .addLore(ChatColor.DARK_PURPLE + "The murderous axe of werewolves")
            .addFlags(ItemFlag.HIDE_ATTRIBUTES)
            .addDamage(2031)
            .setCost(3)
            .setShopType("basic")
            .setShopSlot(5)
            .onEntityDamageEntity(event -> { //todo db
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
                    if (damager.getInventory().getItemInMainHand().getType().equals(Material.NETHERITE_AXE)) {
                        game.getMap().getWorld().playSound(damager.getLocation(), Sound.ITEM_TOTEM_USE, 0.6f, 1);
                        WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());

                        if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                            event.setCancelled(true);
                        }
                        else if (game.isNight() && data.hasActiveProtection()) {
                            event.setCancelled(true);
                            data.setHasActiveProtection(false);
                            target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
                        }
                        else {
                            target.setHealth(0);
                        }
                    }
                }
            })
            .build("werewolf_axe");

    public static final WerewolfItem SKELETON_SLICER = WerewolfItem.create(Material.WOODEN_SWORD, 1)
            .addName(ChatColor.GOLD + "Skeleton Slicer")
            .addLore(ChatColor.BLUE + "Kills skeletons faster")
            .addEnchantment(Enchantment.DAMAGE_UNDEAD, 5)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .addDamage(19)
            .setCost(4)
            .setShopType("basic")
            .setShopSlot(1)
            .build("skeleton_slicer");

    public static final WerewolfItem INVISIBILITY_POTION = WerewolfItem.create(Material.POTION, 1)
            .addName(ChatColor.GOLD + "Invisibility Potion")
            .addLore(ChatColor.BLUE + "Become invisible")
            .addLore(ChatColor.GRAY + "Active for 20 seconds")
            .addPotionEffect(PotionEffectType.INVISIBILITY, 400, 0, false, false, true)
            .setPotionColor(Color.AQUA)
            .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(4)
            .setShopType("special")
            .setShopSlot(4)
            .build("invisibility_potion");

    public static final WerewolfItem SWIFTNESS_POTION = WerewolfItem.create(Material.POTION, 1)
            .addName(ChatColor.GOLD + "Swiftness Potion")
            .addLore(ChatColor.BLUE + "Minor speed boost during the entire game")
            .addPotionEffect(PotionEffectType.SPEED, 72000, 0, false, false, false)
            .setPotionColor(Color.GRAY)
            .addFlags(ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(2)
            .setShopType("special")
            .setShopSlot(3)
            .build("swiftness_potion");

    public static final WerewolfItem LIGHT_OF_REVELATION = WerewolfItem.create(Material.SUNFLOWER, 1)
            .addName(ChatColor.GOLD + "Light Of Revelation")
            .addLore(ChatColor.GREEN + "Right click to use")
            .addLore(ChatColor.BLUE + "Applies glowing to all players except user")
            .addLore(ChatColor.GRAY + "Active for 30 seconds")
            .setCost(3)
            .setShopType("special")
            .setShopSlot(1)
            .onPlayerInteract(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem() != null && event.getItem().getType() == Material.SUNFLOWER) {
                        Player user = event.getPlayer();
                        for (Player player : game.getParticipants()) {
                            if (!player.equals(user)) {
                                if (game.getDataMap().get(player.getUniqueId()).isAlive()) {
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 600, 0, false, false, false));
                                }
                            }
                        }
                        user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
                        game.getMap().getWorld().playSound(user.getLocation(), Sound.ENTITY_ZOMBIE_VILLAGER_CURE, 0.6f,1);
                        WerewolfUtil.sendPluginText(user, "Players are revealed");
                    }
                }
            })
            .build("light_of_revelation");

    public static final WerewolfItem PROTECTION = WerewolfItem.create(Material.ARMOR_STAND, 1)
            .addName(ChatColor.GOLD + "Protection")
            .addLore(ChatColor.GREEN + "Right click to use")
            .addLore(ChatColor.BLUE + "Cancel axe or bow damage once")
            .addLore(ChatColor.GRAY + "Active during one night")
            .addLore(ChatColor.GRAY + "Non-stackable")
            .addLore(ChatColor.GRAY + "Villager-only item")
            .setCost(4)
            .setShopType("special")
            .setShopSlot(2)
            .onPlayerInteract(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem() != null && event.getItem().getType() == Material.ARMOR_STAND) {
                        Player user = event.getPlayer();
                        if (!game.isNight()) {
                            WerewolfUtil.sendPluginText(user, "You cannot use this item during day time", ChatColor.RED);
                            return;
                        }
                        WerewolfPlayerData data = game.getDataMap().get(user.getUniqueId());
                        if (data.hasAlreadyUsedProtection()) {
                            WerewolfUtil.sendPluginText(user, "You have already used one protection this night", ChatColor.RED);
                        }
                        else if (data.getRole() == Role.VILLAGER || data.getRole() == Role.POSSESSED) {
                            data.setHasActiveProtection(true);
                            data.setHasAlreadyUsedProtection(true);
                            user.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2400, 5, false, false, false));
                            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
                            game.getMap().getWorld().playSound(user.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f,1);
                            WerewolfUtil.sendPluginText(user, "Protection activated", ChatColor.GREEN);
                        }
                        else {
                            data.setHasAlreadyUsedProtection(true);
                            user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
                            game.getMap().getWorld().playSound(user.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.8f,1);
                            WerewolfUtil.sendPluginText(user, "Protection will have no effect", ChatColor.RED);
                        }
                    }
                }
            })
            .build("protection");

    public static final WerewolfItem DIVINATION = WerewolfItem.create(Material.HEART_OF_THE_SEA, 1)
            .addName(ChatColor.GOLD + "Divination")
            .addLore(ChatColor.GREEN + "The item will disappear when purchased")
            .addLore(ChatColor.GREEN + "It will add 1 to your divination counter")
            .addLore(ChatColor.BLUE + "Unveil the identity of a player")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(6)
            .setShopType("special")
            .setShopSlot(0)
            .build("divination");

    public static final WerewolfItem TRAITORS_GUIDE = WerewolfItem.create(Material.BOOK, 1)
            .addName(ChatColor.DARK_RED + "Traitor's Guide")
            .addLore(ChatColor.GREEN + "Right click to use")
            .addLore(ChatColor.DARK_PURPLE + "Get the name of one Werewolf")
            .addLore(ChatColor.GRAY + "Same name can be given many times")
            .addLore(ChatColor.GRAY + "Only the Traitor can use this item")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(4)
            .setShopType("special")
            .setShopSlot(8)
            .onPlayerInteract(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    if (event.getItem() != null && event.getItem().getType() == Material.BOOK) {
                        Player user = event.getPlayer();

                        user.getInventory().getItemInMainHand().setAmount(user.getInventory().getItemInMainHand().getAmount()-1);
                        game.getMap().getWorld().playSound(user.getLocation(), Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 0.8f,1);
                        WerewolfUtil.sendPluginText(user, "Players are revealed");
                    }
                }
            })
            .build("traitors_guide");

    public static final WerewolfItem HOLY_STAR = WerewolfItem.create(Material.NETHER_STAR, 1)
            .addName(ChatColor.GOLD + "Holy Star")
            .addLore(ChatColor.GREEN + "Hit a player to use")
            .addLore(ChatColor.BLUE + "Can kill a vampire even during night time")
            .addLore(ChatColor.GRAY + "When used on non-vampire item is lost")
            .setCost(2)
            .setShopType("special")
            .setShopSlot(6)
            .onEntityDamageEntity(event -> { //todo db
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
                    ItemStack item = damager.getInventory().getItemInMainHand();
                    if (item.getType().equals(Material.NETHER_STAR)) {
                        game.getMap().getWorld().playSound(damager.getLocation(), Sound.ITEM_SHIELD_BREAK, 1,1);
                        item.setAmount(item.getAmount() - 1);

                        if (game.getDataMap().get(target.getUniqueId()).getRole() == Role.VAMPIRE) {
                            target.setHealth(0);
                        }
                    }
                }
            })
            .build("holy_star");

    public static final WerewolfItem SNEAK_NOTICE = WerewolfItem.create(Material.GLOBE_BANNER_PATTERN, 1)
            .addName(ChatColor.GOLD + "Sneak Notice")
            .addLore(ChatColor.GREEN + "Right click to use")
            .addLore(ChatColor.BLUE + "Notifies when divination is used on you")
            .addLore(ChatColor.GRAY + "Active during one night")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS)
            .setCost(2)
            .setShopType("special")
            .setShopSlot(7)
            .build("sneak_notice");

    public static final WerewolfItem ASH_OF_THE_DEAD = WerewolfItem.create(Material.GUNPOWDER, 1)
            .addName(ChatColor.GOLD + "Ash of the Dead")
            .addLore(ChatColor.GREEN + "Right click to use")
            .addLore(ChatColor.BLUE + "Identify dead and alive players")
            .addEnchantment(Enchantment.LUCK, 1)
            .addFlags(ItemFlag.HIDE_ENCHANTS)
            .setCost(3)
            .setShopType("special")
            .setShopSlot(5)
            .build("ash_of_the_dead");

    public static final WerewolfItem CURSE_SPEAR = WerewolfItem.create(Material.TRIDENT, 1)
            .addName(ChatColor.GOLD + "Curse Spear")
            .addLore(ChatColor.GREEN + "Hit or throw to enemy to use")
            .addLore(ChatColor.BLUE + "First hit on a player curses him")
            .addLore(ChatColor.BLUE + "Hitting a cursed player will kill him")
            .addLore(ChatColor.GRAY + "Protection and Vampire can negate second hit")
            .addLore(ChatColor.GRAY + "Breaks on usage")
            .addDamage(250)
            .setCost(3)
            .setShopType("basic")
            .setShopSlot(6)
            .onEntityDamageEntity(event -> { //todo db
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                if ((event.getDamager() instanceof Player damager) && (event.getEntity() instanceof Player target)) {
                    if (damager.getInventory().getItemInMainHand().getType().equals(Material.TRIDENT)) {
                        World world = game.getMap().getWorld();
                        WerewolfPlayerData data = game.getDataMap().get(target.getUniqueId());

                        if (data.isCursed()) {
                            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                                return;
                            }
                            if (game.isNight() && data.hasActiveProtection()) {
                                data.setHasActiveProtection(false);
                                target.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                WerewolfUtil.sendPluginText(target, "Protection was activated", ChatColor.GREEN);
                            }
                            else {
                                world.playSound(target.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f,1);
                                target.setHealth(0);
                            }
                        }
                        else {
                            world.playSound(target.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f,1);
                            data.setCursed(true);
                        }
                    }
                }
            })
            .onProjectileHit(event -> {
                WerewolfGame game = WerewolfGame.getInstance();
                if (!game.isPlaying()) return;
                Projectile projectile = event.getEntity();
                if ((projectile instanceof Trident)) {
                    if (event.getHitEntity() instanceof Player player) {
                        World world = game.getMap().getWorld();
                        WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());

                        if (data.isCursed()) {
                            if (game.isNight() && data.getRole() == Role.VAMPIRE) {
                                return;
                            }
                            if (game.isNight() && data.hasActiveProtection()) {
                                data.setHasActiveProtection(false);
                                player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                                WerewolfUtil.sendPluginText(player, "Protection was activated", ChatColor.GREEN);
                            }
                            else {
                                world.playSound(player.getLocation(), Sound.ITEM_SHIELD_BREAK, 0.6f,1);
                                player.setHealth(0);
                            }
                        }
                        else {
                            world.playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, 0.6f,1);
                            data.setCursed(true);
                        }
                    }
                    projectile.remove();
                }
            })
            .build("curse_spear");
}
