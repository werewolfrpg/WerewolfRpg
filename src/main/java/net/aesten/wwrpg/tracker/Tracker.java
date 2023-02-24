package net.aesten.wwrpg.tracker;

import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class Tracker {
    private final Map<UUID, String> specificDeathCauses = new HashMap<>();
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

    public Map<UUID, String> getSpecificDeathCauses() {
        return specificDeathCauses;
    }

    public void setResults(Role winningRole) {
        playerStats.values().forEach(
                stats -> {
                    if (winningRole == null) {
                        stats.setResult(Result.CANCELLED);
                    }
                    else if (stats.getResult() == null) {
                        if (WerewolfUtil.areSameFaction(stats.getRole(), winningRole)) {
                            stats.setResult(Result.VICTORY);
                        }
                        else {
                            stats.setResult(Result.DEFEAT);
                        }
                    }
                }
            );
    }
}
