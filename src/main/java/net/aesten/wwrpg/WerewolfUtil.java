package net.aesten.wwrpg;

import net.aesten.wwrpg.engine.Role;
import net.aesten.wwrpg.engine.WerewolfTeams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class WerewolfUtil {
    public static void sendPluginText(Player player, String message) {
        sendPluginText(player, message, ChatColor.AQUA);
    }

    public static void sendPluginText(Player player, String message, ChatColor color) {
        player.sendMessage(wwrpg.COLOR + wwrpg.LOG + color + message);
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

    public static void removeAllPotionEffectsFromAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeAllPotionEffectsFromPlayer(player);
        }
    }

    public static void removeAllPotionEffectsFromPlayer(Player player) {
        for (PotionEffectType type : PotionEffectType.values()) {
            if (player.hasPotionEffect(type)) {
                player.removePotionEffect(type);
            }
        }
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
