package net.aesten.werewolfrpg.plugin.statistics;

import net.aesten.werewolfrpg.bot.WerewolfBot;
import net.aesten.werewolfrpg.backend.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.aesten.werewolfrpg.plugin.data.Faction;
import net.aesten.werewolfrpg.plugin.data.Role;
import net.aesten.werewolfrpg.plugin.utilities.WerewolfUtil;
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
                });
    }

    public void sendDataToDatabase(WerewolfGame game, Role winner) {
        QueryManager.addMatchRecord(game.getMatchId(), game.getStartTime(), game.getEndTime(), winner);
        playerStats.values().forEach(stats -> {
            QueryManager.addPlayerMatchRecord(game.getMatchId(), stats);
            int gainedScore = WerewolfGame.getScoreManager().getCalculatedScore(stats);
            scoreDetails.put(UUID.fromString(stats.getPlayerId()), new ScoreDetail(QueryManager.addPlayerScore(stats.getPlayerId(), gainedScore), gainedScore));
        });
        WerewolfRpg.logConsole("Saved match " + game.getMatchId() + " in database");
    }

    public void logMatchResult(WerewolfGame game, Role winner) {
        WerewolfBot bot = WerewolfRpg.getBot();
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
                    embed.addField(name, status + "\n" + sd.score + " (+" + sd.gain + ")", true);
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

    private static final class ScoreDetail {
        public int score;
        public int gain;

        public ScoreDetail(int score, int gain) {
            this.score = score;
            this.gain = gain;
        }
    }
}
