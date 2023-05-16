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
                });
    }

    public void sendDataToDatabase(WerewolfGame game, Role winner) {
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

    public void logMatchResult(WerewolfGame game, Role winner) {
        WerewolfBot bot = WerewolfBot.getBot();
        if (bot == null) return;

        EmbedBuilder embed = new EmbedBuilder();

        String result;
        if (winner == null) {
            result = "Cancelled";
            embed.setColor(Color.YELLOW);
        }
        else {
            result = winner.name + " Victory";
            embed.setColor(Color.CYAN);
        }

        long time = (game.getEndTime().getTime() - game.getStartTime().getTime());
        String duration = DurationFormatUtils.formatDuration(time, "HH:mm:ss");

        embed.setTitle(result);
        embed.setDescription("Map: " + game.getMap().getName() + "\nDuration: " + duration + "\nMatchId: " + game.getMatchId());

        AtomicInteger c = new AtomicInteger();
        for (Faction faction : WerewolfGame.getTeamsManager().getFactions().values()) {
            if (faction.getInitialPlayers().size() > 0) {
                embed.addField("===============", "", true);
                embed.addField(faction.getTeam().getName() + " (" + faction.getInitialPlayers().size() + ")", "", true);
                embed.addField("===============", "", true);
                faction.getInitialPlayers().forEach((id, name) -> {
                    String status;
                    if (playerStats.get(id).getResult() == Result.DISCONNECTED) status = "*Disconnected*";
                    else if (game.getDataMap().get(id).isAlive()) status = "*Alive*";
                    else status = "*Dead*";
                    ScoreDetail sd = scoreDetails.get(id);
                    if (sd == null) {
                        embed.addField(name, status, true);
                    } else {
                        embed.addField(name, status + "\n" + sd.score + " (+" + sd.gain + ")", true);
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
        }

        bot.getLc().sendMessage("").setEmbeds(embed.build()).queue();
    }

    public Map<UUID, Integer> getGains() {
        return scoreDetails.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue().gain));
    }

    private record ScoreDetail(int score, int gain) {}
}