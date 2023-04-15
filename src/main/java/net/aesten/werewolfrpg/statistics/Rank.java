package net.aesten.werewolfrpg.statistics;

import net.md_5.bungee.api.ChatColor;

public enum Rank {
    BEGINNER(ChatColor.of("#11b1e7") + "[Beginner] " + ChatColor.RESET),
    NOVICE(ChatColor.of("#11b1e7") + "[Novice] " + ChatColor.RESET),
    APPRENTICE(ChatColor.of("#11b1e7") + "[Apprentice] " + ChatColor.RESET),
    INTERMEDIATE(ChatColor.of("#e28621") + "[Intermediate] " + ChatColor.RESET),
    SKILLED(ChatColor.of("#e28621") + "[Skilled] " + ChatColor.RESET),
    EXPERIENCED(ChatColor.of("#e28621") + "[Experienced] " + ChatColor.RESET),
    VETERAN(ChatColor.of("#e92269") + "[Veteran] " + ChatColor.RESET),
    EXPERT(ChatColor.of("#e92269") + "[Expert] " + ChatColor.RESET),
    ELITE(ChatColor.of("#e92269") + "[Elite] " + ChatColor.RESET),
    LEGENDARY(ChatColor.of("#ffad00") + "[Legendary] " + ChatColor.RESET);

    private final String prefix;

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
