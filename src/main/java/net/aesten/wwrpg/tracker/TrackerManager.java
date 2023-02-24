package net.aesten.wwrpg.tracker;

import net.aesten.wwrpg.data.Role;
import org.bukkit.entity.Player;

import java.util.*;

public class TrackerManager {
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();

    private final List<Event> history = new ArrayList<>();

    public PlayerStats addPlayer(Player player) {
        playerStats.put(player.getUniqueId(), new PlayerStats());
        return playerStats.get(player.getUniqueId());
    }

    public PlayerStats getPlayerStats(UUID id) {
        return playerStats.get(id);
    }

    public void addEvent(Event event) {
        history.add(event);
    }

    public List<Event> getHistory() {
        return history;
    }

    public void setResults(Role winningRole) {
        playerStats.values().forEach(stats -> {
            if (stats.getResult() == null) {
                if (winningRole == null) {
                    stats.setResult(Result.CANCELLED);
                }
                else if (winningRole == Role.VAMPIRE) {
                    if (stats.getRole() )
                }
            }
                }
        );
    }
}
