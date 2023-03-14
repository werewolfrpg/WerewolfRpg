package net.aesten.werewolfrpg.tracker;

import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import org.bukkit.entity.Player;

import java.util.*;

public class Tracker {
    private final Map<UUID, AbstractMap.SimpleEntry<String, UUID>> specificDeathCauses = new HashMap<>();
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();

    public PlayerStats addPlayer(Player player) {
        playerStats.put(player.getUniqueId(), new PlayerStats(player.getUniqueId().toString()));
        return playerStats.get(player.getUniqueId());
    }

    public PlayerStats getPlayerStats(UUID id) {
        return playerStats.get(id);
    }

    public Map<UUID, AbstractMap.SimpleEntry<String, UUID>> getSpecificDeathCauses() {
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

    public void sendDataToDatabase(String matchId) {
        playerStats.values().forEach(stats -> QueryManager.addMatchRecord(matchId, stats));
    }
}
