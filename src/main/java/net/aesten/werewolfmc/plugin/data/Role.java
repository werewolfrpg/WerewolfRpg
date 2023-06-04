package net.aesten.werewolfmc.plugin.data;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public enum Role {
    VILLAGER("Villager", "#55ff55", Faction.VILLAGER, true, false),
    WEREWOLF("Werewolf", "#aa0000", Faction.WEREWOLF, true, true),
    TRAITOR("Traitor", "#ff55ff", Faction.WEREWOLF, false, false),
    VAMPIRE("Vampire", "#55ffff", Faction.OTHER, true, false),
    SERVANT("Servant", "#00aaaa", Faction.OTHER, false, true),
    POSSESSED("Possessed", "#ffff55", Faction.VILLAGER, true, false);

    private final String roleName;
    private final String roleColor;
    private final Faction faction;
    private final boolean countInFaction;
    private final Team team;

    Role(String roleName, String roleColor, Faction faction, boolean countInFaction, boolean canSeeAllies) {
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
        return ChatColor.of(roleColor);
    }

    public String getColorCode() {
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
