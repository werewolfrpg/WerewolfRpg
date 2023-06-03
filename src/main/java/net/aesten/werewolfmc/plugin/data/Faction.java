package net.aesten.werewolfmc.plugin.data;

import org.bukkit.ChatColor;

public enum Faction {
    VILLAGER("Villagers", ChatColor.GREEN),
    WEREWOLF("Werewolves", ChatColor.DARK_RED),
    OTHER("Third Party", ChatColor.LIGHT_PURPLE);

    private final String factionName;
    private final ChatColor factionColor;

    Faction(String factionName, ChatColor factionColor) {
        this.factionName = factionName;
        this.factionColor = factionColor;
    }

    public String getName() {
        return factionName;
    }

    public ChatColor getColor() {
        return factionColor;
    }
}

