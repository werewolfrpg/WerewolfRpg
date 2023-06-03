package net.aesten.werewolfmc.plugin.data;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public enum Role {
    VILLAGER("Villager", ChatColor.GREEN, Faction.VILLAGER, true, false),
    WEREWOLF("Werewolf", ChatColor.DARK_RED, Faction.WEREWOLF, true, true),
    TRAITOR("Traitor", ChatColor.LIGHT_PURPLE, Faction.WEREWOLF, false, false),
    VAMPIRE("Vampire", ChatColor.AQUA, Faction.OTHER, true, false),
    SERVANT("Servant", ChatColor.DARK_AQUA, Faction.OTHER, false, true),
    POSSESSED("Possessed", ChatColor.YELLOW, Faction.VILLAGER, true, false);

    private final String roleName;
    private final ChatColor roleColor;
    private final Faction faction;
    private final boolean countInFaction;
    private final Team team;

    Role(String roleName, ChatColor roleColor, Faction faction, boolean countInFaction, boolean canSeeAllies) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        this.roleName = roleName;
        this.roleColor = roleColor;
        this.faction = faction;
        this.countInFaction = countInFaction;
        team = board.registerNewTeam(roleName);

        team.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, canSeeAllies ? Team.OptionStatus.FOR_OTHER_TEAMS : Team.OptionStatus.NEVER);
        team.setCanSeeFriendlyInvisibles(canSeeAllies);
    }

    public String getName() {
        return roleName;
    }

    public ChatColor getColor() {
        return roleColor;
    }

    public Faction getFaction() {
        return faction;
    }

    public boolean isCountInFaction() {
        return countInFaction;
    }

    public Team getTeam() {
        return team;
    }

    public Role getBelievedRole() {
        return this == Role.POSSESSED ? Role.VILLAGER : this;
    }

    public Role getDivinationRole() {
        return switch (this) {
            case TRAITOR -> VILLAGER;
            case POSSESSED -> WEREWOLF;
            case SERVANT -> VAMPIRE;
            default -> this;
        };
    }
}
