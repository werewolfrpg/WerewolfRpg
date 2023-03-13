package net.aesten.wwrpg.tracker;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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

    private static String getJson(PlayerStats stats) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(stats);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendJsonData() {
        String root = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        String path = root + File.separator + "stats";
        if (new File(path).mkdir()) {
            WerewolfRpg.logConsole("Initialized stats folder");
        }
        String matchId = UUID.randomUUID().toString();
        playerStats.forEach((playerId, stats) -> {
            String fullPath = path + File.separator + matchId + File.separator + playerId.toString() + ".json";
            saveJsonData(fullPath, getJson(stats));
        });
        playerStats.values().stream().map(Tracker::getJson).forEach(json -> saveJsonData(json, json));
    }

    private static void saveJsonData(String fullPath, String json) {
        try {
            FileWriter writer = new FileWriter(fullPath);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            WerewolfRpg.logConsole("Saving stats as json failed for: " + fullPath);
        }
    }
}
