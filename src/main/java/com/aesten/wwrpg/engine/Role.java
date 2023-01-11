package com.aesten.wwrpg.engine;

import org.bukkit.ChatColor;

public enum Role {
    VILLAGER("Villager", ChatColor.GREEN),
    WEREWOLF("Werewolf", ChatColor.DARK_RED),
    TRAITOR("Traitor", ChatColor.LIGHT_PURPLE),
    VAMPIRE("Vampire", ChatColor.DARK_PURPLE),
    POSSESSED("Possessed", ChatColor.YELLOW),
    SPECTATOR("Spectator", ChatColor.GRAY);

    public final String name;
    public final ChatColor color;

    Role(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }
}
