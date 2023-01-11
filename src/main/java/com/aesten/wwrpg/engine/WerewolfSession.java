package com.aesten.wwrpg.engine;

import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class WerewolfSession {
    private static final List<String> sessions = new ArrayList<>();
    private final String sessionId;
    private final WerewolfPlayer owner;
    private final List<WerewolfPlayer> sessionPlayers;
    private final List<WerewolfPlayer> spectators;
    private WerewolfRound currentRound;
    private World selectedWorld;

    public WerewolfSession(String sessionId, WerewolfPlayer owner) {
        this.sessionId = sessionId;
        this.owner = owner;
        this.sessionPlayers = new ArrayList<>();
        this.spectators = new ArrayList<>();
        sessions.add(sessionId);
    }

    public String getSessionId() {
        return sessionId;
    }

    public WerewolfPlayer getOwner() {
        return owner;
    }

    public List<WerewolfPlayer> getSessionPlayers() {
        return sessionPlayers;
    }

    public List<WerewolfPlayer> getSpectators() {
        return spectators;
    }

    public WerewolfRound getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(WerewolfRound currentRound) {
        this.currentRound = currentRound;
    }

    public static List<String> getSessions() {
        return sessions;
    }

    public static boolean removeSession(String sessionId) {
        return sessions.remove(sessionId);
    }
}
