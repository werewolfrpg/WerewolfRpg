package net.aesten.wwrpg.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Objects;

public class WerewolfTeams {
    private static Team villagers;
    private static Team werewolves;
    private static Team traitors;
    private static Team vampires;
    private static Team possessed;

    public static Team getTeam(Role role) {
        return switch(role) {
            case WEREWOLF -> werewolves;
            case TRAITOR -> traitors;
            case VAMPIRE -> vampires;
            case POSSESSED -> possessed;
            default -> villagers;
        };
    }

    public static void init() {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        villagers = board.registerNewTeam("Villager");
        villagers.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        villagers.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        villagers.setCanSeeFriendlyInvisibles(false);
        villagers.setColor(ChatColor.GREEN);

        werewolves = board.registerNewTeam("Werewolf");
        werewolves.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        werewolves.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        werewolves.setCanSeeFriendlyInvisibles(true);
        werewolves.setColor(ChatColor.GREEN);

        traitors = board.registerNewTeam("Traitor");
        traitors.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        traitors.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        traitors.setCanSeeFriendlyInvisibles(false);
        traitors.setColor(ChatColor.GREEN);

        vampires = board.registerNewTeam("Vampire");
        vampires.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        vampires.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        vampires.setCanSeeFriendlyInvisibles(false);
        vampires.setColor(ChatColor.GREEN);

        possessed = board.registerNewTeam("Possessed");
        possessed.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        possessed.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        possessed.setCanSeeFriendlyInvisibles(false);
        possessed.setColor(ChatColor.GREEN);
    }

    public static void clear() {
        removeAllEntries(villagers);
        removeAllEntries(werewolves);
        removeAllEntries(traitors);
        removeAllEntries(vampires);
        removeAllEntries(possessed);
    }

    private static void removeAllEntries(Team team) {
        for (String s : team.getEntries()) {
            team.removeEntry(s);
        }
    }
}
