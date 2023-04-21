package net.aesten.werewolfrpg.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "match_records")
public class MatchRecord {
    @Id
    @Column(name = "match_id", nullable = false)
    private UUID matchId;

    @Column(name = "start_time", nullable = false)
    private Timestamp startTime;

    @Column(name = "end_time", nullable = false)
    private Timestamp endTime;

    @Column(name = "winning_faction", nullable = false)
    private String endReason;

    public MatchRecord(UUID matchId, Timestamp startTime, Timestamp endTime, String endReason) {
        this.matchId = matchId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.endReason = endReason;
    }

    public MatchRecord() {
    }

    public UUID getMatchId() {
        return matchId;
    }

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
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

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }
}
