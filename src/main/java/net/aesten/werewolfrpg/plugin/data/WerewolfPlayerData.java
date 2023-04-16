package net.aesten.werewolfrpg.plugin.data;

public class WerewolfPlayerData {
    private Role role;
    private boolean forceMute;
    private boolean isAlive;
    private boolean isCursed;
    private boolean isStunned;
    private boolean hasActiveSneakNotice;
    private boolean hasActiveProtection;
    private boolean hasAlreadyUsedProtection;
    private boolean hasAlreadyUsedDivination;
    private boolean hasBeenDivinated;
    private int remainingDivinations;

    public WerewolfPlayerData() {
        this.forceMute = false;
        this.isAlive = true;
        this.isCursed = false;
        this.isStunned = false;
        this.hasActiveSneakNotice = false;
        this.hasActiveProtection = false;
        this.hasAlreadyUsedProtection = false;
        this.hasAlreadyUsedDivination = false;
        this.hasBeenDivinated = false;
        this.remainingDivinations = 0;
    }

    public void resetTemporaryValues() {
        this.hasActiveSneakNotice = false;
        this.hasActiveProtection = false;
        this.hasAlreadyUsedProtection = false;
        this.hasAlreadyUsedDivination = false;
        this.hasBeenDivinated = false;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isForceMute() {
        return forceMute;
    }

    public void setForceMute(boolean forceMute) {
        this.forceMute = forceMute;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
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

    public void setHasActiveSneakNotice(boolean hasActiveSneakNotice) {
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