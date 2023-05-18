package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.util.UUID;

@Entity(name = "PlayerData")
@Table(name = "player_data")
public class PlayerData {
    @Id
    @Column(name = "minecraft_id", nullable = false)
    @SerializedName("minecraftId")
    private UUID mcId;

    @Column(name = "minecraft_username", nullable = false)
    @SerializedName("minecraftUsername")
    private String mcName;

    @Column(name = "discord_id", nullable = false, unique = true)
    @SerializedName("discordId")
    private long dcId;

    @Column(name = "score", nullable = false)
    @SerializedName("score")
    private int score;

    public PlayerData(UUID mcId, String mcName, long dcId, int score) {
        this.mcId = mcId;
        this.mcName = mcName;
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

    public String getMcName() {
        return mcName;
    }

    public void setMcName(String mcName) {
        this.mcName = mcName;
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
