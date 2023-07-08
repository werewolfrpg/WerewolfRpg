package net.aesten.werewolfmc.plugin.data;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public enum Role {
    VILLAGER("Villager", "#55ff55", Faction.VILLAGER, true, false, "The villager is the original inhabitant of the peaceful village, seeking to defend it. His key power is called a \"Divination\", used to unveil the nature of others."),
    WEREWOLF("Werewolf", "#aa0000", Faction.WEREWOLF, true, true, "The werewolf is an undercover enemy of the village, working to eliminate its dwellers. His key power resides in the \"Werewolf Axe\", a strong weapon of murder."),
    TRAITOR("Traitor", "#ff55ff", Faction.WEREWOLF, false, false, "The traitor is a villager that decided to side by the werewolves by betraying its village. He therefore appears as an innocent upon a \"Divination\". He also has access to the \"Traitor's Guide\", a magical book only him can use to identify his new allies."),
    VAMPIRE("Vampire", "#55ffff", Faction.OUTSIDER, true, false, "The vampire is an extremely strong nightly creature that can overtake the village if kept alive. His invulnerability during night time is his strongest asset, but can be countered by the \"Holy Star\", specifically created to exterminate evil."),
    SERVANT("Servant", "#00aaaa", Faction.OUTSIDER, false, true, "The servant can be created by the vampire to support him in fulfilling his goal. Although appearing as a vampire upon \"Divination\", his powers are weak enough that he can be killed normally and the \"Holy Star\" has no effect."),
    POSSESSED("Possessed", "#ffff55", Faction.VILLAGER, true, false, "The possessed is a villager whose soul has been tainted by the werewolves. He will appear as a werewolf upon \"Divination\" although being part of the villagers.");

    private final String roleName;
    private final String roleColor;
    private final Faction faction;
    private final boolean countInFaction;
    private final Team team;
    private final String description;

    Role(String roleName, String roleColor, Faction faction, boolean countInFaction, boolean canSeeAllies, String description) {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        this.roleName = roleName;
        this.roleColor = roleColor;
        this.faction = faction;
        this.countInFaction = countInFaction;
        this.description = description;
        this.team = board.registerNewTeam(roleName);

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

    public String getDescription() {
        return description;
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
