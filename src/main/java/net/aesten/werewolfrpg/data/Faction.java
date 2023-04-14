package net.aesten.werewolfrpg.data;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class Faction {
    private final Team team;
    private final Map<UUID, String> initialPlayers;
    private final List<Player> players;

    public Faction(Team team) {
        this.team = team;
        this.initialPlayers = new HashMap<>();
        this.players = new ArrayList<>();
    }

    public Team getTeam() {
        return team;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<UUID, String> getInitialPlayers() {
        return initialPlayers;
    }

    public void register(Player player) {
        team.addEntry(player.getName());
        players.add(player);
        initialPlayers.put(player.getUniqueId(), player.getName());
    }

    public void remove(Player player) {
        team.removeEntry(player.getName());
        players.remove(player);
    }

    public void clear() {
        removeAllEntries(team);
        players.clear();
        initialPlayers.clear();
    }

    private void removeAllEntries(Team team) {
        for (String s : team.getEntries()) {
            team.removeEntry(s);
        }
    }
}
