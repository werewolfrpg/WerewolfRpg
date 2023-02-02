package net.aesten.wwrpg.engine;

import java.util.Map;
import java.util.UUID;

public class WerewolfPlayerData {
    public static Map<UUID, WerewolfPlayerData> dataMap;
    private Role role;
    private boolean isDead;
    private boolean isCursed;
    private boolean isStunned;
    private boolean hasActiveSneakNotice;
    private boolean hasActiveProtection;
    private boolean hasAlreadyUsedProtection;
    private boolean hasAlreadyUsedDivination;
    private boolean hasBeenDivinated;
    private int remainingDivinations;

    public WerewolfPlayerData() {
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

    public static void resetTemporaryValues() {
        for (WerewolfPlayerData data : dataMap.values()) {
            data.hasActiveSneakNotice = false;
            data.hasActiveProtection = false;
            data.hasAlreadyUsedProtection = false;
            data.hasAlreadyUsedDivination = false;
            data.hasBeenDivinated = false;
        }
    }

    public static WerewolfPlayerData getData(UUID id) {
        return dataMap.get(id);
    }

    public static Map<UUID, WerewolfPlayerData> getDataMap() {
        return dataMap;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public boolean hasActiveSneakNotice() {
        return hasActiveSneakNotice;
    }

    public void hasActiveSneakNotice(boolean hasActiveSneakNotice) {
        this.hasActiveSneakNotice = hasActiveSneakNotice;
    }

    public boolean hasActiveProtection() {
        return hasActiveProtection;
    }

    public void setHasActiveProtection(boolean hasActiveProtection) {
        this.hasActiveProtection = hasActiveProtection;
    }

    public boolean hasAlreadyUsedProtection() {
        return hasAlreadyUsedProtection;
    }

    public void setHasAlreadyUsedProtection(boolean hasAlreadyUsedProtection) {
        this.hasAlreadyUsedProtection = hasAlreadyUsedProtection;
    }

    public boolean hasAlreadyUsedDivination() {
        return hasAlreadyUsedDivination;
    }

    public void setHasAlreadyUsedDivination(boolean hasAlreadyUsedDivination) {
        this.hasAlreadyUsedDivination = hasAlreadyUsedDivination;
    }

    public boolean hasBeenDivinated() {
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