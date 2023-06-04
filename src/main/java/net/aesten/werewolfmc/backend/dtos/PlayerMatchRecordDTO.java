package net.aesten.werewolfmc.backend.dtos;

import com.google.gson.annotations.SerializedName;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;

import java.sql.Timestamp;
import java.util.UUID;

public class PlayerMatchRecordDTO {
    @SerializedName("matchId")
    private UUID matchId;
    @SerializedName("map")
    private String mapName;
    @SerializedName("startTime")
    private Timestamp startTime;
    @SerializedName("endTime")
    private Timestamp endTime;
    @SerializedName("winnerFaction")
    private Faction winner;
    @SerializedName("role")
    private Role role;

    public PlayerMatchRecordDTO(MatchRecord record, Role role) {
        matchId = record.getMatchId();
        mapName = record.getMapName();
        startTime = record.getStartTime();
        endTime = record.getEndTime();
        winner = record.getWinner();
        this.role = role;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
