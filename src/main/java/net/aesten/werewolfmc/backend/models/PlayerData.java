package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "player_data")
public class PlayerData {
    @Id
    @Column(name = "minecraft_id", nullable = false)
    @SerializedName("minecraft_id")
    private UUID mcId;

    @Column(name = "discord_id", nullable = false)
    @SerializedName("discord_id")
    private long dcId;

    @Column(name = "score", nullable = false)
    @SerializedName("score")
    private int score;

    public PlayerData(UUID mcId, long dcId, int score) {
        this.mcId = mcId;
        this.dcId = dcId;
        this.score = score;
    }

    public PlayerData() {

    }

    public UUID getMcId() {
        return mcId;
    }

    public void setMcId(UUID mcId) {
        this.mcId = mcId;
    }

    public long getDcId() {
        return dcId;
    }

    public void setDcId(long dcId) {
        this.dcId = dcId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
