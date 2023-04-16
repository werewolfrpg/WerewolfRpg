package net.aesten.werewolfrpg.plugin.statistics;

import net.aesten.werewolfrpg.plugin.data.Role;

@SuppressWarnings({"all"})
public class PlayerStats {
    private final String playerId;
    private Role role;
    private Result result;

    //basic stats
    private int kills = 0;
    private String killerId; //can be null
    private String deathCause; //can be null
    private int killedBasicSkeletons = 0;
    private int killedLuckySkeletons = 0;
    private int killedSpecialSkeletons = 0;
    private int basicSkeletonEmeraldDrops = 0;

    //item stats
    private int steaksEaten = 0;
    private int asheUsed = 0;
    private int divinationUsed = 0;
    private int invisibilityUsed = 0;
    private int swiftnessUsed = 0;
    private int revelationUsed = 0;
    private int traitorsGuideUsed = 0;

    //effectiveness
    private int curseSpearMeleeUsed = 0;
    private int curseSpearMeleeCurses = 0;
    private int curseSpearMeleeKills = 0;
    private int curseSpearThrowUsed = 0;
    private int curseSpearThrowHits = 0;
    private int curseSpearThrowCurses = 0;
    private int curseSpearThrowKills = 0;
    private int arrowUsed = 0;
    private int arrowHits = 0;
    private int arrowKills = 0;
    private int stunGrenadeUsed = 0;
    private int stunGrenadeHits = 0;
    private int stunGrenadeHitTargets = 0;
    private int holyStarUsed = 0;
    private int holyStarKills = 0;
    private int protectionUsed = 0;
    private int protectionActivated = 0;
    private int protectionTriggered = 0;
    private int sneakNoticeUsed = 0;
    private int sneakNoticeTriggered = 0;
    private int werewolfAxeUsed = 0;
    private int werewolfAxeKills = 0;

    public PlayerStats(String playerId) {
        this.playerId = playerId;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Role getRole() {
        return role;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public Result getResult() {
        return result;
    }

    public void addKills() {
        this.kills += 1;
    }

    public void setKiller(String killerId) {
        this.killerId = killerId;
    }

    public void setDeathCause(String deathCause) {
        this.deathCause = deathCause;
    }

    public void addKilledBasicSkeletons() {
        this.killedBasicSkeletons += 1;
    }

    public void addKilledLuckySkeletons() {
        this.killedLuckySkeletons += 1;
    }

    public void addKilledSpecialSkeletons() {
        this.killedSpecialSkeletons += 1;
    }
    
    public void addBasicSkeletonEmeraldDrops() {
        this.basicSkeletonEmeraldDrops += 1;
    }
    
    public void addSteaksEaten() {
        this.steaksEaten += 1;
    }
    
    public void addAsheUsed() {
        this.asheUsed += 1;
    }
    
    public void addDivinationUsed() {
        this.divinationUsed += 1;
    }
    
    public void addInvisibilityUsed() {
        this.invisibilityUsed += 1;
    }
    
    public void addSwiftnessUsed() {
        this.swiftnessUsed += 1;
    }
    
    public void addRevelationUsed() {
        this.revelationUsed += 1;
    }
    
    public void addTraitorsGuideUsed() {
        this.traitorsGuideUsed += 1;
    }
    
    public void addCurseSpearMeleeUsed(boolean cursed, boolean killed) {
        this.curseSpearMeleeUsed += 1;
        if (cursed) this.curseSpearMeleeCurses += 1;
        if (killed) this.curseSpearMeleeKills += 1;
    }
    
    public void addCurseSpearThrowUsed() {
        this.curseSpearThrowUsed += 1;
    }
    
    public void addCurseSpearThrowHits(boolean cursed, boolean killed) {
        this.curseSpearThrowHits += 1;
        if (cursed) this.curseSpearThrowCurses += 1;
        if (killed) this.curseSpearThrowKills += 1;
    }

    public void addArrowUsed() {
        this.arrowUsed += 1;
    }

    public void addArrowHits(boolean killed) {
        this.arrowHits += 1;
        if (killed) this.arrowKills += 1;
    }

    public void addStunGrenadeUsed() {
        this.stunGrenadeUsed += 1;
    }
    
    public void addStunGrenadeHits(int targets) {
        this.stunGrenadeHits += 1;
        this.stunGrenadeHitTargets += targets;
    }

    public void addHolyStarUsed(boolean killed) {
        this.holyStarUsed += 1;
        if (killed) this.holyStarKills += 1;
    }
    
    public void addProtectionUsed(boolean activated) {
        this.protectionUsed += 1;
        if (activated) this.protectionActivated += 1;
    }
    
    public void addProtectionTriggered() {
        this.protectionTriggered += 1;
    }

    public void addSneakNoticeUsed() {
        this.sneakNoticeUsed += 1;
    }
    
    public void addSneakNoticeTriggered() {
        this.sneakNoticeTriggered += 1;
    }

    public void addWerewolfAxeUsed(boolean killed) {
        this.werewolfAxeUsed += 1;
        if (killed) this.werewolfAxeKills += 1;
    }

    //getters for jackson

    public String getPlayerId() {
        return playerId;
    }

    public int getKills() {
        return kills;
    }

    public String getKillerId() {
        return killerId;
    }

    public String getDeathCause() {
        return deathCause;
    }

    public int getKilledBasicSkeletons() {
        return killedBasicSkeletons;
    }

    public int getKilledLuckySkeletons() {
        return killedLuckySkeletons;
    }

    public int getKilledSpecialSkeletons() {
        return killedSpecialSkeletons;
    }

    public int getBasicSkeletonEmeraldDrops() {
        return basicSkeletonEmeraldDrops;
    }

    public int getSteaksEaten() {
        return steaksEaten;
    }

    public int getAsheUsed() {
        return asheUsed;
    }

    public int getDivinationUsed() {
        return divinationUsed;
    }

    public int getInvisibilityUsed() {
        return invisibilityUsed;
    }

    public int getSwiftnessUsed() {
        return swiftnessUsed;
    }

    public int getRevelationUsed() {
        return revelationUsed;
    }

    public int getTraitorsGuideUsed() {
        return traitorsGuideUsed;
    }

    public int getCurseSpearMeleeUsed() {
        return curseSpearMeleeUsed;
    }

    public int getCurseSpearMeleeCurses() {
        return curseSpearMeleeCurses;
    }

    public int getCurseSpearMeleeKills() {
        return curseSpearMeleeKills;
    }

    public int getCurseSpearThrowUsed() {
        return curseSpearThrowUsed;
    }

    public int getCurseSpearThrowHits() {
        return curseSpearThrowHits;
    }

    public int getCurseSpearThrowCurses() {
        return curseSpearThrowCurses;
    }

    public int getCurseSpearThrowKills() {
        return curseSpearThrowKills;
    }

    public int getArrowUsed() {
        return arrowUsed;
    }

    public int getArrowHits() {
        return arrowHits;
    }

    public int getArrowKills() {
        return arrowKills;
    }

    public int getStunGrenadeUsed() {
        return stunGrenadeUsed;
    }

    public int getStunGrenadeHits() {
        return stunGrenadeHits;
    }

    public int getStunGrenadeHitTargets() {
        return stunGrenadeHitTargets;
    }

    public int getHolyStarUsed() {
        return holyStarUsed;
    }

    public int getHolyStarKills() {
        return holyStarKills;
    }

    public int getProtectionUsed() {
        return protectionUsed;
    }

    public int getProtectionActivated() {
        return protectionActivated;
    }

    public int getProtectionTriggered() {
        return protectionTriggered;
    }

    public int getSneakNoticeUsed() {
        return sneakNoticeUsed;
    }

    public int getSneakNoticeTriggered() {
        return sneakNoticeTriggered;
    }

    public int getWerewolfAxeUsed() {
        return werewolfAxeUsed;
    }

    public int getWerewolfAxeKills() {
        return werewolfAxeKills;
    }
}
