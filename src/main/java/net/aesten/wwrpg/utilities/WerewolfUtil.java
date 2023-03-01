package net.aesten.wwrpg.utilities;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.data.Role;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WerewolfUtil {
    public static void sendPluginText(Player player, String message) {
        sendPluginText(player, message, ChatColor.AQUA);
    }

    public static void sendPluginText(Player player, String message, ChatColor color) {
        player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + color + message);
    }

    public static void sendCommandHelp(CommandSender sender, String message) {
        if (sender instanceof Player player) sendPluginText(player, message, ChatColor.YELLOW);
    }

    public static void sendCommandError(CommandSender sender, String message) {
        if (sender instanceof Player player) sendErrorText(player, message);
    }

    public static void sendCommandText(CommandSender sender, String message) {
        if (sender instanceof Player player) sendPluginText(player, message);
    }

    public static void sendErrorText(Player player, String message) {
        sendPluginText(player, message, ChatColor.RED);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle, 2, 2, 40);
    }

    public static Vector getVectorAbove(Location blockLocation) {
        return new Vector(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()).add(new Vector(0, 1, 0));
    }

    public static void updateSkull(World world, Vector skullCoordinates, Player player) {
        Block block = world.getBlockAt(skullCoordinates.toLocation(world));
        block.setType(Material.PLAYER_HEAD);
        if (block.getState() instanceof Skull skull) {
            skull.setOwningPlayer(player);
            skull.update();
        }
    }

    public static void resetSkull(World world, Vector skullCoordinates) {
        Block block = world.getBlockAt(skullCoordinates.toLocation(world));
        block.setType(Material.SKELETON_SKULL);
    }

    public static ArmorStand summonNameTagArmorStand(World world, Vector coordinates, Vector offset, String name) {
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(coordinates.add(offset).toLocation(world), EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(ChatColor.AQUA + name);
        return armorStand;
    }

    public static void removeAllPotionEffectsFromPlayer(Player player) {
        for (PotionEffectType type : PotionEffectType.values()) {
            if (player.hasPotionEffect(type)) {
                player.removePotionEffect(type);
            }
        }
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound , 0.6f, 1);
    }

    public static void runDelayedTask(int delay, Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(WerewolfRpg.getPlugin(), delay);
    }

    public static boolean areSameFaction(Role role1, Role role2) {
        if (role1 == role2) return true;
        else return (role1 == Role.WEREWOLF && role2 == Role.TRAITOR) ||
                (role1 == Role.TRAITOR && role2 == Role.WEREWOLF) ||
                (role1 == Role.VILLAGER && role2 == Role.POSSESSED) ||
                (role1 == Role.POSSESSED && role2 == Role.VILLAGER);
    }

    public static boolean sameItem(ItemStack item1, ItemStack item2) {
        return (item1.getItemMeta() == item2.getItemMeta());
    }
}
