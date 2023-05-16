package net.aesten.werewolfmc.backend.dtos;

import com.google.gson.annotations.SerializedName;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.statistics.Rank;

import java.util.UUID;

public class LeaderboardDTO {
    @SerializedName("minecraft_id")
    private UUID mcId;
    @SerializedName("minecraft_username")
    private String mcUsername;
    @SerializedName("score")
    private int score;
    @SerializedName("rank")
    private Rank rank;
    @SerializedName("games_played")
    private long gamesPlayed;
    @SerializedName("games_won")
    private long gamesWon;

    public LeaderboardDTO(PlayerData data, long gamesPlayed, long gamesWon) {
        this.mcId = data.getMcId();
        this.mcUsername = data.getMcName();
        this.score = data.getScore();
        this.rank = WerewolfGame.getScoreManager().getScoreRank(score);
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
    }

    public UUID getMcId() {
        return mcId;
    }

    public void setMcId(UUID mcId) {
        this.mcId = mcId;
    }

    public String getMcUsername() {
        return mcUsername;
    }

    public void setMcUsername(String mcUsername) {
        this.mcUsername = mcUsername;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
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
