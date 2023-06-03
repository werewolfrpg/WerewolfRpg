package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import net.aesten.werewolfmc.plugin.data.Faction;

import java.sql.Timestamp;
import java.util.UUID;

@Entity(name = "MatchRecord")
@Table(name = "match_records")
public class MatchRecord {
    @Id
    @Column(name = "match_id", nullable = false)
    @SerializedName("matchId")
    private UUID matchId;

    @Column(name = "map", nullable = false)
    @SerializedName("map")
    private String mapName;

    @Column(name = "start_time", nullable = false)
    @SerializedName("startTime")
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    @SerializedName("endTime")
    private Timestamp endTime;

    @Column(name = "winner")
    @SerializedName("winner")
    private Faction winner;

    public MatchRecord(UUID matchId, String mapName, Timestamp startTime, Timestamp endTime, Faction winner) {
        this.matchId = matchId;
        this.mapName = mapName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.winner = winner;
    }

    public MatchRecord() {
    }

    public UUID getMatchId() {
        return matchId;
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Faction getWinner() {
        return winner;
    }

    public void setWinner(Faction winner) {
        this.winner = winner;
    }
}
