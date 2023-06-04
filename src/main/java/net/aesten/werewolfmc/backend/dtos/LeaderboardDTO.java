package net.aesten.werewolfmc.backend.dtos;

import com.google.gson.annotations.SerializedName;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.statistics.Rank;

import java.util.UUID;

public class LeaderboardDTO {
    @SerializedName("minecraftId")
    private UUID mcId;
    @SerializedName("score")
    private int score;
    @SerializedName("ranking")
    private int ranking;
    @SerializedName("title")
    private Rank title;
    @SerializedName("gamesPlayed")
    private long gamesPlayed;
    @SerializedName("gamesWon")
    private long gamesWon;

    public LeaderboardDTO(PlayerData data, long gamesPlayed, long gamesWon, int ranking) {
        this.mcId = data.getMcId();
        this.score = data.getScore();
        this.ranking = ranking;
        this.title = WerewolfGame.getScoreManager().getScoreRank(score);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public UUID getMcId() {
        return mcId;
    }

    public void setMcId(UUID mcId) {
        this.mcId = mcId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Rank getTitle() {
        return title;
    }

    public void setTitle(Rank title) {
        this.title = title;
    }

    public long getGamesPlayed() {
        return gamesPlayed;
    }

    public void setGamesPlayed(long gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public long getGamesWon() {
        return gamesWon;
    }

    public void setGamesWon(long gamesWon) {
        this.gamesWon = gamesWon;
    }
}
