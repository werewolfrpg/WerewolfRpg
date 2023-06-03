package net.aesten.werewolfmc.plugin.statistics;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Tracker {
    private final Map<UUID, AbstractMap.SimpleEntry<String, UUID>> specificDeathCauses = new HashMap<>();
    private final Map<UUID, PlayerStats> playerStats = new HashMap<>();
    private final Map<UUID, ScoreDetail> scoreDetails = new HashMap<>();

    public PlayerStats addPlayer(Player player) {
        playerStats.put(player.getUniqueId(), new PlayerStats(player.getUniqueId()));
        return playerStats.get(player.getUniqueId());
    }

    public PlayerStats getPlayerStats(UUID id) {
        return playerStats.get(id);
    }

    public Map<UUID, AbstractMap.SimpleEntry<String, UUID>> getSpecificDeathCauses() {
        return specificDeathCauses;
    }

    public void setResults(Faction winningFaction) {
        playerStats.values().forEach(stats -> {
                    if (winningFaction == null) {
                        stats.setResult(Result.CANCELLED);
                    }
                    else if (stats.getResult() == null) {
                        if (stats.getRole().getFaction() == winningFaction) {
                            stats.setResult(Result.VICTORY);
                        }
                        else {
                            stats.setResult(Result.DEFEAT);
                        }
                    }
        });
    }

    public void sendDataToDatabase(WerewolfGame game, Faction winner) {
        WerewolfBackend backend = WerewolfBackend.getBackend();
        backend.getMrc().recordMatch(new MatchRecord(game.getMatchId(), game.getMap().getName(), game.getStartTime(), game.getEndTime(), winner));
        playerStats.values().forEach(stats -> {
            int gainedScore = WerewolfGame.getScoreManager().getCalculatedScore(stats);
            if (winner != null) {
                stats.setGain(gainedScore);
                scoreDetails.put(stats.getPlayerId(), new ScoreDetail(backend.getPdc().addScoreToPlayer(stats.getPlayerId(), gainedScore).join().getScore(), gainedScore));
            } else {
                stats.setGain(0);
            }
            stats.setMatchId(game.getMatchId());
            backend.getPsc().savePlayerStats(stats).join();
        });
        WerewolfPlugin.logConsole("Saved match " + game.getMatchId() + " in database");
    }

    public void logMatchResult(WerewolfGame game, Faction winner) {
        WerewolfBot bot = WerewolfBot.getBot();
        if (bot == null) return;

        EmbedBuilder embed = new EmbedBuilder();

        String result;
        if (winner == null) {
            result = "Cancelled";
            embed.setColor(Color.YELLOW);
        }
        else {
            result = winner.getName() + " Victory";
            embed.setColor(Color.CYAN);
        }

        long time = (game.getEndTime().getTime() - game.getStartTime().getTime());
        String duration = DurationFormatUtils.formatDuration(time, "HH:mm:ss");

        embed.setTitle(result);
        embed.setDescription("Map: " + game.getMap().getName() + "\nDuration: " + duration + "\nMatchId: " + game.getMatchId());

        AtomicInteger c = new AtomicInteger();
        WerewolfGame.getTeamsManager().getData().forEach((faction, playerDataList) -> {
            if (!playerDataList.isEmpty()) {
                embed.addField("===============", "", true);
                embed.addField(faction.getName() + " (" + playerDataList.size() + ")", "", true);
                embed.addField("===============", "", true);

                playerDataList.forEach(playerData -> {
                    String status = game.getDataMap().get(playerData.getUuid()).isAlive() ? "A" : "D";
                    ScoreDetail sd = scoreDetails.get(playerData.getUuid());
                    if (sd == null) {
                        embed.addField(playerData.getName() + " (" + status + ")", playerData.getRole().getName(), true);
                    } else {
                        embed.addField(playerData.getName() + " (" + status + ")", playerData.getRole().getName() + "\n" + sd.score + " (+" + sd.gain + ")", true);
                    }
                    c.getAndIncrement();
                });

                if (c.get() % 3 != 0) {
                    embed.addField("", "", true);
                }
                if (c.get() % 3 == 1) {
                    embed.addField("", "", true);
                }
                c.set(0);
            }
        });

        if (WerewolfBackend.getBackend() != null) {
            if (!game.getMap().getImage().equals("")) {
                embed.setImage(WerewolfBackend.getConfig().getBackendUrl().get() + File.separator + game.getMap().getImage());
            }
        }

        bot.getLc().sendMessage("").setEmbeds(embed.build()).queue();
    }

    public Map<UUID, Integer> getGains() {
        return scoreDetails.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().gain));
    }

    private record ScoreDetail(int score, int gain) {}
}
