package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.statistics.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "player_stats")
public class PlayerStats {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @SerializedName("id")
    private int id;
    @Column(name = "player_id", nullable = false)
    @SerializedName("playerId")
    private UUID playerId;
    @Column(name = "match_id", nullable = false)
    @SerializedName("matchId")
    private UUID matchId;
    @Column(name = "role", nullable = false)
    @SerializedName("role")
    private Role role;
    @Column(name = "result", nullable = false)
    @SerializedName("result")
    private Result result;

    //additional data
    @Column(name = "score_gain", nullable = false)
    @SerializedName("scoreGain")
    private int gain = 0;

    //basic stats
    @Column(name = "kills", nullable = false)
    @SerializedName("kills")
    private int kills = 0;
    @Column(name = "killer_id")
    @SerializedName("killerId")
    private UUID killerId;
    @Column(name = "death_cause")
    @SerializedName("deathCause")
    private String deathCause;

    //skeleton stats
    @SerializedName("skeletons")
    private SkeletonStats skeletonStats = new SkeletonStats();

    @Embeddable
    public static final class SkeletonStats {
        @Column(name = "killed_basic_skeletons", nullable = false)
        @SerializedName("killedBasicSkeletons")
        private int killedBasicSkeletons = 0;
        @Column(name = "killed_lucky_skeletons", nullable = false)
        @SerializedName("killedLuckySkeletons")
        private int killedLuckySkeletons = 0;
        @Column(name = "killed_special_skeletons", nullable = false)
        @SerializedName("killedSpecialSkeletons")
        private int killedSpecialSkeletons = 0;
        @Column(name = "basic_skeleton_emerald_drops", nullable = false)
        @SerializedName("basicSkeletonEmeraldDrops")
        private int basicSkeletonEmeraldDrops = 0;

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

        public void setKilledBasicSkeletons(int killedBasicSkeletons) {
            this.killedBasicSkeletons = killedBasicSkeletons;
        }

        public void setKilledLuckySkeletons(int killedLuckySkeletons) {
            this.killedLuckySkeletons = killedLuckySkeletons;
        }

        public void setKilledSpecialSkeletons(int killedSpecialSkeletons) {
            this.killedSpecialSkeletons = killedSpecialSkeletons;
        }

        public void setBasicSkeletonEmeraldDrops(int basicSkeletonEmeraldDrops) {
            this.basicSkeletonEmeraldDrops = basicSkeletonEmeraldDrops;
        }
    }

    //item stats
    @OneToMany(cascade = CascadeType.ALL)
    @SerializedName("items")
    private List<ItemStats> itemStats = initItemStats();

    private List<ItemStats> initItemStats() {
        List<ItemStats> stats = new ArrayList<>();

        ItemStats steakStat = new ItemStats("Exquisite Meat");
        steakStat.getStats().put("eaten", 0);
        stats.add(steakStat);

        ItemStats ashStat = new ItemStats("Ash of the Dead");
        ashStat.getStats().put("used", 0);
        stats.add(ashStat);

        ItemStats divStat = new ItemStats("Divination");
        divStat.getStats().put("used", 0);
        stats.add(divStat);

        ItemStats invisibilityStat = new ItemStats("Invisibility Potion");
        invisibilityStat.getStats().put("used", 0);
        stats.add(invisibilityStat);

        ItemStats swiftnessStat = new ItemStats("Swiftness Potion");
        swiftnessStat.getStats().put("used", 0);
        stats.add(swiftnessStat);

        ItemStats revelationStat = new ItemStats("Revelation");
        revelationStat.getStats().put("used", 0);
        stats.add(revelationStat);

        ItemStats traitorStat = new ItemStats("Traitor's Guide");
        traitorStat.getStats().put("used", 0);
        stats.add(traitorStat);

        ItemStats curseSpearMeleeStat = new ItemStats("Curse Spear (Melee)");
        curseSpearMeleeStat.getStats().put("used", 0);
        curseSpearMeleeStat.getStats().put("cursed", 0);
        curseSpearMeleeStat.getStats().put("killed", 0);
        stats.add(curseSpearMeleeStat);

        ItemStats curseSpearThrownStat = new ItemStats("Curse Spear (Thrown)");
        curseSpearThrownStat.getStats().put("used", 0);
        curseSpearThrownStat.getStats().put("hit", 0);
        curseSpearThrownStat.getStats().put("cursed", 0);
        curseSpearThrownStat.getStats().put("killed", 0);
        stats.add(curseSpearThrownStat);

        ItemStats bowStat = new ItemStats("Hunter's Bow");
        bowStat.getStats().put("used", 0);
        bowStat.getStats().put("hit", 0);
        bowStat.getStats().put("killed", 0);
        stats.add(bowStat);

        ItemStats grenadeStat = new ItemStats("Stun Grenade");
        grenadeStat.getStats().put("used", 0);
        grenadeStat.getStats().put("hit", 0);
        grenadeStat.getStats().put("hitTargets", 0);
        stats.add(grenadeStat);

        ItemStats starStat = new ItemStats("Holy Star");
        starStat.getStats().put("used", 0);
        starStat.getStats().put("killed", 0);
        stats.add(starStat);

        ItemStats protectionStat = new ItemStats("Protection");
        protectionStat.getStats().put("used", 0);
        protectionStat.getStats().put("activated", 0);
        protectionStat.getStats().put("triggered", 0);
        stats.add(protectionStat);

        ItemStats noticeStat = new ItemStats("Sneak Notice");
        noticeStat.getStats().put("used", 0);
        noticeStat.getStats().put("triggered", 0);
        stats.add(noticeStat);

        ItemStats axeStat = new ItemStats("Werewolf Axe");
        axeStat.getStats().put("used", 0);
        axeStat.getStats().put("killed", 0);
        stats.add(axeStat);

        ItemStats trapStat = new ItemStats("Werewolf Trap");
        trapStat.getStats().put("placed", 0);
        trapStat.getStats().put("activated", 0);
        stats.add(trapStat);

        return stats;
    }

    public ItemStats getByItemName(String itemName) {
        return this.itemStats.stream().filter(is -> is.getItemName().equals(itemName)).findAny().orElse(new ItemStats());
    }

    private void incrementValue(String itemName, String field) {
        this.itemStats.stream().filter(is -> is.getItemName().equals(itemName)).findAny().ifPresent(is -> is.getStats().put(field, is.getStats().get(field) + 1));
    }

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

    public void setResult(Result result) {
        this.result = result;
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
        this.skeletonStats.killedBasicSkeletons += 1;
    }

    public void addKilledLuckySkeletons() {
        this.skeletonStats.killedLuckySkeletons += 1;
    }

    public void addKilledSpecialSkeletons() {
        this.skeletonStats.killedSpecialSkeletons += 1;
    }
    
    public void addBasicSkeletonEmeraldDrops() {
        this.skeletonStats.basicSkeletonEmeraldDrops += 1;
    }

    public void addSteaksEaten() {
        incrementValue("Exquisite Meat", "eaten");
    }
    
    public void addAsheUsed() {
        incrementValue("Ash of the Dead", "used");
    }
    
    public void addDivinationUsed() {
        incrementValue("Divination", "used");
    }
    
    public void addInvisibilityUsed() {
        incrementValue("Invisibility Potion", "used");
    }
    
    public void addSwiftnessUsed() {
        incrementValue("Swiftness Potion", "used");
    }
    
    public void addRevelationUsed() {
        incrementValue("Revelation", "used");
    }
    
    public void addTraitorsGuideUsed() {
        incrementValue("Traitor's Guide", "used");
    }
    
    public void addCurseSpearMeleeUsed(boolean cursed, boolean killed) {
        incrementValue("Curse Spear (Melee)", "used");
        if (cursed) incrementValue("Curse Spear (Melee)", "cursed");
        if (killed) incrementValue("Curse Spear (Melee)", "killed");
    }
    
    public void addCurseSpearThrowUsed() {
        incrementValue("Curse Spear (Thrown)", "used");
    }
    
    public void addCurseSpearThrowHits(boolean cursed, boolean killed) {
        incrementValue("Curse Spear (Thrown)", "hit");
        if (cursed) incrementValue("Curse Spear (Thrown)", "cursed");
        if (killed) incrementValue("Curse Spear (Thrown)", "killed");
    }

    public void addArrowUsed() {
        incrementValue("Hunter's Bow", "used");
    }

    public void addArrowHits(boolean killed) {
        incrementValue("Hunter's Bow", "hit");
        if (killed) incrementValue("Hunter's Bow", "killed");
    }

    public void addStunGrenadeUsed() {
        incrementValue("Stun Grenade", "used");
    }
    
    public void addStunGrenadeHits(int targets) {
        incrementValue("Stun Grenade", "hit");
        incrementValue("Stun Grenade", "hitTargets");
    }

    public void addHolyStarUsed(boolean killed) {
        incrementValue("Holy Star", "used");
        if (killed) incrementValue("Holy Star", "killed");
    }
    
    public void addProtectionUsed(boolean activated) {
        incrementValue("Protection", "used");
        if (activated) incrementValue("Protection", "activated");
    }
    
    public void addProtectionTriggered() {
        incrementValue("Protection", "triggered");
    }

    public void addSneakNoticeUsed() {
        incrementValue("Sneak Notice", "used");
    }
    
    public void addSneakNoticeTriggered() {
        incrementValue("Sneak Notice", "triggered");
    }

    public void addWerewolfAxeUsed(boolean killed) {
        incrementValue("Werewolf Axe", "used");
        if (killed) incrementValue("Werewolf Axe", "killed");
    }

    public void addWerewolfTrapUsed() {
        incrementValue("Werewolf Trap", "placed");
    }

    public void addWerewolfTrapTriggered() {
        incrementValue("Werewolf Trap", "activated");
    }

    public void setGain(int gain) {
        this.gain = gain;
    }

    public int getId() {
        return id;
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public UUID getMatchId() {
        return matchId;
    }

    public Role getRole() {
        return role;
    }

    public Result getResult() {
        return result;
    }

    public int getGain() {
        return gain;
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

    public SkeletonStats getSkeletonStats() {
        return skeletonStats;
    }

    public List<ItemStats> getItemStats() {
        return itemStats;
    }
}
