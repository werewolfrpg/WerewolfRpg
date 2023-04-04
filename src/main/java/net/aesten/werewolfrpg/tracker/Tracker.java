package net.aesten.werewolfrpg.tracker;

import net.aesten.werewolfbot.WerewolfBot;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.awt.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

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

    public void sendDataToDatabase(WerewolfGame game, Role winner) {
        QueryManager.addMatchRecord(game.getMatchId().replace("-", ""), game.getStartTime(), game.getEndTime(), winner);
        playerStats.values().forEach(stats -> QueryManager.addPlayerMatchRecord(game.getMatchId().replace("-", ""), stats));
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

        embed.setAuthor("WerewolfRPG");
        embed.setTitle(result);
        embed.setDescription("Duration: " + duration);
        embed.setFooter("MatchId: " + game.getMatchId());

        AtomicInteger c = new AtomicInteger();
        for (Team team : WerewolfGame.getTeamsManager().getTeams()) {
            Set<String> entries = team.getEntries();
            if (entries.size() > 0) {
                embed.addField("===============", "", true);
                embed.addField(team.getName() + " (" + entries.size() + ")", "", true);
                embed.addField("===============", "", true);
                entries.forEach(s -> {
                    Player player = Bukkit.getPlayerExact(s);
                    String status = "*Disconnected*";
                    if (player != null) {
                        if (game.getDataMap().get(player.getUniqueId()).isAlive()) status = "*Alive*";
                        else status = "*Dead*";
                    }
                    embed.addField(s, status, true);
                    c.getAndIncrement();
                });
                if (c.get() % 3 != 0) {
                    embed.addField("", "", true);
                }
                if (c.get() % 3 == 1) {
                    embed.addField("", "", true);
                }
            }
        }

        //embed.setImage(); todo add per map image and url?

        bot.getCurrentSession().getLc().sendMessage("").setEmbeds(embed.build()).queue();
    }
}
