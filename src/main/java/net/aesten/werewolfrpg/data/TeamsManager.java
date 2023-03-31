package net.aesten.werewolfrpg.data;

import net.aesten.werewolfrpg.core.WerewolfGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamsManager {
    private final Map<Role, List<UUID>> factions;
    private final Team villagers;
    private final Team werewolves;
    private final Team traitors;
    private final Team vampires;
    private final Team possessed;
    private final Team spectators;

    public TeamsManager() {
        factions = initFactions();
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

        spectators = board.registerNewTeam("Spectator");
        spectators.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        spectators.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        spectators.setCanSeeFriendlyInvisibles(true);
        spectators.setColor(ChatColor.RED);
    }

    public List<Team> getTeams() {
        return List.of(villagers, werewolves, traitors, vampires, possessed);
    }

    public Team getTeam(Role role) {
        return switch(role) {
            case WEREWOLF -> werewolves;
            case TRAITOR -> traitors;
            case VAMPIRE -> vampires;
            case POSSESSED -> possessed;
            case VILLAGER -> villagers;
        };
    }

    public Team getSpectators() {
        return spectators;
    }

    public void registerPlayerRole(Player player, Role role) {
        getTeam(role).addEntry(player.getName());
        getFaction(role.factionRole()).add(player.getUniqueId());
    }

    public void addPlayerToSpectator(Player player) {
        spectators.addEntry(player.getName());
    }

    public void playerDied(Player player) {
        Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
        getFaction(role.factionRole()).remove(player.getUniqueId());
    }

    public void clear() {
        removeAllEntries(villagers);
        removeAllEntries(werewolves);
        removeAllEntries(traitors);
        removeAllEntries(vampires);
        removeAllEntries(possessed);
        clearFactions();
    }

    private void removeAllEntries(Team team) {
        for (String s : team.getEntries()) {
            team.removeEntry(s);
        }
    }

    private Map<Role, List<UUID>> initFactions() {
        return Map.of(
                Role.VILLAGER, new ArrayList<>(),
                Role.WEREWOLF, new ArrayList<>(),
                Role.VAMPIRE, new ArrayList<>()
        );
    }

    public List<UUID> getFaction(Role role) {
        return factions.get(role);
    }

    private void clearFactions() {
        for (Role role : factions.keySet()) {
            factions.get(role).clear();
        }
    }

    public void unregisterAll() {
        villagers.unregister();
        werewolves.unregister();
        traitors.unregister();
        vampires.unregister();
        possessed.unregister();
        spectators.unregister();
    }
}
