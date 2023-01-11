package com.aesten.wwrpg.engine;

import org.bukkit.entity.Player;

import java.util.UUID;

public class WerewolfPlayer {
    private final UUID id;
    private Player player;
    private String playerName;
    private Role role;
    private boolean isPlaying;

    private boolean isDead;
    private boolean isCursed;
    private boolean isStunned;
    private boolean hasActiveSneakNotice;
    private boolean hasActiveProtection;
    private boolean hasAlreadyUsedProtection;
    private boolean hasAlreadyUsedDivination;
    private boolean hasBeenDivinated;
    private int remainingDivinations;

    public WerewolfPlayer(Player player) {
        this.player = player;
        this.id = player.getUniqueId();
        this.playerName = player.getName();
        this.isDead = false;
        this.isCursed = false;
        this.isStunned = false;
        this.hasActiveSneakNotice = false;
        this.hasActiveProtection = false;
        this.hasAlreadyUsedProtection = false;
        this.hasAlreadyUsedDivination = false;
        this.hasBeenDivinated = false;
        this.remainingDivinations = 0;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public UUID getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public boolean isCursed() {
        return isCursed;
    }

    public void setCursed(boolean cursed) {
        isCursed = cursed;
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void setStunned(boolean stunned) {
        isStunned = stunned;
    }

    public boolean isHasActiveSneakNotice() {
        return hasActiveSneakNotice;
    }

    public void setHasActiveSneakNotice(boolean hasActiveSneakNotice) {
        this.hasActiveSneakNotice = hasActiveSneakNotice;
    }

    public boolean isHasActiveProtection() {
        return hasActiveProtection;
    }

    public void setHasActiveProtection(boolean hasActiveProtection) {
        this.hasActiveProtection = hasActiveProtection;
    }

    public boolean isHasAlreadyUsedProtection() {
        return hasAlreadyUsedProtection;
    }

    public void setHasAlreadyUsedProtection(boolean hasAlreadyUsedProtection) {
        this.hasAlreadyUsedProtection = hasAlreadyUsedProtection;
    }

    public boolean isHasAlreadyUsedDivination() {
        return hasAlreadyUsedDivination;
    }

    public void setHasAlreadyUsedDivination(boolean hasAlreadyUsedDivination) {
        this.hasAlreadyUsedDivination = hasAlreadyUsedDivination;
    }

    public boolean isHasBeenDivinated() {
        return hasBeenDivinated;
    }

    public void setHasBeenDivinated(boolean hasBeenDivinated) {
        this.hasBeenDivinated = hasBeenDivinated;
    }

    public int getRemainingDivinations() {
        return remainingDivinations;
    }

    public void setRemainingDivinations(int remainingDivinations) {
        this.remainingDivinations = remainingDivinations;
    }
}