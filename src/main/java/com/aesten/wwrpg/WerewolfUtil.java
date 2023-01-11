package com.aesten.wwrpg;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class WerewolfUtil {
    public static void sendPluginText(Player player, String message) {
        sendPluginText(player, message, ChatColor.AQUA);
    }

    public static void sendPluginText(Player player, String message, ChatColor color) {
        player.sendMessage(wwrpg.COLOR + wwrpg.LOG + color + message);
    }


}
