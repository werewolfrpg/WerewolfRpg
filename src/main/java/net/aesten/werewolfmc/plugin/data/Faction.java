package net.aesten.werewolfmc.plugin.data;


import net.md_5.bungee.api.ChatColor;

public enum Faction {
    VILLAGER("Villagers", "#55ff55", "Members of the villager faction need to work together to eliminate players of the opposing factions. A smart, efficient and cooperative gameplay is required to seize the victory."),
    WEREWOLF("Werewolves","#aa0000", "Members of the werewolf faction should swiftly take down enemy players before they get identified and eliminated. Take full advantage of the powerful weapons to take down enemies."),
    OUTSIDER("Outsiders", "#ffff55", "Members of the outsider faction are some third-party players who seek to steal the victory from the two main factions. Success can be earned from playing carefully and adapting quickly.");

    private final String factionName;
    private final String factionColor;
    private final String factionDescription;

    Faction(String factionName, String factionColor, String factionDescription) {
        this.factionName = factionName;
        this.factionColor = factionColor;
        this.factionDescription = factionDescription;
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

    public String getDescription() {
        return factionDescription;
    }
}

