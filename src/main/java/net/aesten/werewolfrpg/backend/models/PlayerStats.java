package net.aesten.werewolfrpg.backend.models;

import jakarta.persistence.*;
import net.aesten.werewolfrpg.plugin.data.Role;
import net.aesten.werewolfrpg.plugin.statistics.Result;

import java.util.UUID;

@Entity
@Table(name = "player_stats")
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Column(name = "player_id", nullable = false)
    private UUID playerId;
    @Column(name = "match_id", nullable = false)
    private UUID matchId;
    @Column(name = "role", nullable = false)
    private Role role;
    @Column(name = "result", nullable = false)
    private Result result;

    //basic stats
    @Column(name = "kills", nullable = false)
    private int kills = 0;
    @Column(name = "killer_id")
    private UUID killerId;
    @Column(name = "death_cause")
    private String deathCause;
    @Column(name = "killed_basic_skeletons", nullable = false)
    private int killedBasicSkeletons = 0;
    @Column(name = "killed_lucky_skeletons", nullable = false)
    private int killedLuckySkeletons = 0;
    @Column(name = "killed_special_skeletons", nullable = false)
    private int killedSpecialSkeletons = 0;
    @Column(name = "basic_skeleton_emerald_drops", nullable = false)
    private int basicSkeletonEmeraldDrops = 0;

    //item stats
    @Column(name = "steaks_eaten", nullable = false)
    private int steaksEaten = 0;
    @Column(name = "ash_used", nullable = false)
    private int ashUsed = 0;
    @Column(name = "divination_used", nullable = false)
    private int divinationUsed = 0;
    @Column(name = "invisibility_used", nullable = false)
    private int invisibilityUsed = 0;
    @Column(name = "swiftness_used", nullable = false)
    private int swiftnessUsed = 0;
    @Column(name = "revelation_used", nullable = false)
    private int revelationUsed = 0;
    @Column(name = "traitors_guide_used", nullable = false)
    private int traitorsGuideUsed = 0;

    //effectiveness
    @Column(name = "curse_spear_melee_used", nullable = false)
    private int curseSpearMeleeUsed = 0;
    @Column(name = "curse_spear_melee_curses", nullable = false)
    private int curseSpearMeleeCurses = 0;
    @Column(name = "curse_spear_melee_kills", nullable = false)
    private int curseSpearMeleeKills = 0;
    @Column(name = "curse_spear_throw_used", nullable = false)
    private int curseSpearThrowUsed = 0;
    @Column(name = "curse_spear_throw_hits", nullable = false)
    private int curseSpearThrowHits = 0;
    @Column(name = "curse_spear_throw_curses", nullable = false)
    private int curseSpearThrowCurses = 0;
    @Column(name = "curse_spear_throw_kills", nullable = false)
    private int curseSpearThrowKills = 0;
    @Column(name = "arrow_used", nullable = false)
    private int arrowUsed = 0;
    @Column(name = "arrow_hits", nullable = false)
    private int arrowHits = 0;
    @Column(name = "arrow_kills", nullable = false)
    private int arrowKills = 0;
    @Column(name = "stun_grenade_used", nullable = false)
    private int stunGrenadeUsed = 0;
    @Column(name = "stun_grenade_hits", nullable = false)
    private int stunGrenadeHits = 0;
    @Column(name = "stun_grenade_hit_targets", nullable = false)
    private int stunGrenadeHitTargets = 0;
    @Column(name = "hole_star_used", nullable = false)
    private int holyStarUsed = 0;
    @Column(name = "hole_star_kills", nullable = false)
    private int holyStarKills = 0;
    @Column(name = "protection_used", nullable = false)
    private int protectionUsed = 0;
    @Column(name = "protection_activated", nullable = false)
    private int protectionActivated = 0;
    @Column(name = "protection_triggered", nullable = false)
    private int protectionTriggered = 0;
    @Column(name = "sneak_notice_used", nullable = false)
    private int sneakNoticeUsed = 0;
    @Column(name = "sneak_notice_trigerred", nullable = false)
    private int sneakNoticeTriggered = 0;
    @Column(name = "werewolf_axe_used", nullable = false)
    private int werewolfAxeUsed = 0;
    @Column(name = "werewolf_axe_kills", nullable = false)
    private int werewolfAxeKills = 0;

    //additional data
    @Column(name = "score_gain", nullable = false)
    private int gain = 0;

    public PlayerStats(UUID playerId) {
        this.playerId = playerId;
    }

    public PlayerStats() {}

    public void setMatchId(UUID matchId) {
        this.matchId = matchId;
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

    public void setKiller(UUID killerId) {
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
        this.ashUsed += 1;
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

    public void setGain(int gain) {
        this.gain = gain;
    }

    //getters for jackson

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public int getKills() {
        return kills;
    }

    public UUID getKillerId() {
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

    public int getAshUsed() {
        return ashUsed;
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

    public int getGain() {
        return gain;
    }
}