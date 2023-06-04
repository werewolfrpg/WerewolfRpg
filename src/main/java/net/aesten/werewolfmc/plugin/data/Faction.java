package net.aesten.werewolfmc.plugin.data;


import net.md_5.bungee.api.ChatColor;

public enum Faction {
    VILLAGER("Villagers", "#55ff55"),
    WEREWOLF("Werewolves","#aa0000"),
    OTHER("Third Party", "#ffff55");

    private final String factionName;
    private final String factionColor;

    Faction(String factionName, String factionColor) {
        this.factionName = factionName;
        this.factionColor = factionColor;
    }

    public String getName() {
        return factionName;
    }

    public ChatColor getColor() {
        return ChatColor.of(factionColor);
    }

    public String getColorCode() {
        return factionColor;
    }
}

