package net.aesten.werewolfmc.plugin.statistics;

import net.md_5.bungee.api.ChatColor;

public enum Rank {
    BEGINNER("Beginner", "#11b1e7"),
    NOVICE("Novice", "#11b1e7"),
    APPRENTICE("Apprentice", "#11b1e7"),
    INTERMEDIATE("Intermediate", "#e28621"),
    SKILLED("Skilled", "#e28621"),
    EXPERIENCED("Experienced", "#e28621"),
    VETERAN("Veteran", "#e92269"),
    EXPERT("Expert", "#e92269"),
    ELITE("Elite", "#e92269"),
    LEGENDARY("Legendary", "#ffad00");

    private final String name;
    private final String colorHex;

    Rank(String name, String colorHex) {
        this.name = name;
        this.colorHex = colorHex;
    }

    public String getPrefix() {
        return ChatColor.of(colorHex) + "[" + name + "] " + ChatColor.RESET;
    }

    public String getName() {
        return name;
    }

    public String getColorHex() {
        return colorHex;
    }
}
