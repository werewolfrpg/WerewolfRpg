package net.aesten.werewolfmc.plugin.data;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamsManager {
    private final Map<Role, Faction> factions;

    public TeamsManager() {
        Scoreboard board = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();
        this.factions = new HashMap<>();

        Team villagers = board.registerNewTeam("Villager");
        villagers.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        villagers.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        villagers.setCanSeeFriendlyInvisibles(false);
        factions.put(Role.VILLAGER, new Faction(villagers));

        Team werewolves = board.registerNewTeam("Werewolf");
        werewolves.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        werewolves.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        werewolves.setCanSeeFriendlyInvisibles(true);
        factions.put(Role.WEREWOLF, new Faction(werewolves));

        Team traitors = board.registerNewTeam("Traitor");
        traitors.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        traitors.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        traitors.setCanSeeFriendlyInvisibles(false);
        factions.put(Role.TRAITOR, new Faction(traitors));

        Team vampires = board.registerNewTeam("Vampire");
        vampires.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        vampires.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        vampires.setCanSeeFriendlyInvisibles(false);
        factions.put(Role.VAMPIRE, new Faction(vampires));

        Team possessed = board.registerNewTeam("Possessed");
        possessed.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        possessed.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);
        possessed.setCanSeeFriendlyInvisibles(false);
        factions.put(Role.POSSESSED, new Faction(possessed));

        Team spectators = board.registerNewTeam("Spectator");
        spectators.setOption(Team.Option.DEATH_MESSAGE_VISIBILITY, Team.OptionStatus.NEVER);
        spectators.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
        spectators.setCanSeeFriendlyInvisibles(true);
        spectators.setColor(ChatColor.GRAY);
        factions.put(Role.SPECTATOR, new Faction(spectators));
    }

    public void registerPlayerRole(Player player, Role role) {
        factions.get(role).register(player);
    }

    public void playerDied(Player player) {
        Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
        factions.get(role).remove(player);
    }

    public void clear() {
        factions.values().forEach(Faction::clear);
    }

    public Faction getFaction(Role role) {
        return factions.get(role);
    }

    public int getFactionSize(Role role) {
        switch (role) {
            case WEREWOLF, VAMPIRE -> {
                return factions.get(role).getPlayers().size();
            }
            case VILLAGER -> {
                return factions.get(role).getPlayers().size() + factions.get(Role.POSSESSED).getPlayers().size();
            }
            default -> {
                return -1;
            }
        }
    }

    public void unregisterAll() {
        factions.values().stream().map(Faction::getTeam).forEach(Team::unregister);
    }

    public Map<Role, Faction> getFactions() {
        return factions;
    }
}
