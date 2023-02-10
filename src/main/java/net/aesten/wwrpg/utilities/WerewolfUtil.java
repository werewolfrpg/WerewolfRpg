package net.aesten.wwrpg.utilities;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.WerewolfTeams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class WerewolfUtil {
    public static void sendPluginText(Player player, String message) {
        sendPluginText(player, message, ChatColor.AQUA);
    }

    public static void sendPluginText(Player player, String message, ChatColor color) {
        player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.LOG + color + message);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle, 2, 2, 40);
    }

    public static void sendErrorToOps(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp()) sendPluginText(player, message, ChatColor.RED);
        }
    }

    public static void updateSignPost(World world, Vector signCoordinates, String... lines) {
        if (world.getBlockAt(signCoordinates.toLocation(world)).getState() instanceof Sign sign) {
            for (int i = 0 ; i < lines.length ; i++) {
                sign.setLine(i, lines[i]);
            }
        }
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

    public static void showMatchRoles() {
        String villagerPlayers = String.join(", ", WerewolfTeams.getTeam(Role.VILLAGER).getEntries());
        String werewolfPlayer = String.join(", ", WerewolfTeams.getTeam(Role.WEREWOLF).getEntries());
        String traitorPlayers = String.join(", ", WerewolfTeams.getTeam(Role.TRAITOR).getEntries());
        String vampirePlayers = String.join(", ", WerewolfTeams.getTeam(Role.VAMPIRE).getEntries());
        String possessedPlayers = String.join(", ", WerewolfTeams.getTeam(Role.POSSESSED).getEntries());

        Bukkit.broadcastMessage("\n");
        Bukkit.broadcastMessage(ChatColor.AQUA + "======WWRPG Match Role======");
        Bukkit.broadcastMessage(ChatColor.GREEN + "Villagers:");
        Bukkit.broadcastMessage(ChatColor.GREEN + villagerPlayers);
        Bukkit.broadcastMessage(ChatColor.DARK_RED + "Werewolves:");
        Bukkit.broadcastMessage(ChatColor.DARK_RED + werewolfPlayer);
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Traitor:");
        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + traitorPlayers);
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Vampire:");
        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + vampirePlayers);
        Bukkit.broadcastMessage(ChatColor.YELLOW + "Possessed:");
        Bukkit.broadcastMessage(ChatColor.YELLOW + possessedPlayers);
        Bukkit.broadcastMessage(ChatColor.AQUA + "======WWRPG Match Role======");
        Bukkit.broadcastMessage("\n");
    }

}
