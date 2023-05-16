package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.statistics.Result;

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
        @SerializedName("killed_basic_skeletons")
        private int killedBasicSkeletons = 0;
        @Column(name = "killed_lucky_skeletons", nullable = false)
        @SerializedName("killed_lucky_skeletons")
        private int killedLuckySkeletons = 0;
        @Column(name = "killed_special_skeletons", nullable = false)
        @SerializedName("killed_special_skeletons")
        private int killedSpecialSkeletons = 0;
        @Column(name = "basic_skeleton_emerald_drops", nullable = false)
        @SerializedName("basic_skeleton_emerald_drops")
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
    @SerializedName("items")
    private ItemStats itemStats = new ItemStats();

    @Embeddable
    public static final class ItemStats {
        //usage stats
        @Column(name = "steaks_eaten", nullable = false)
        @SerializedName("steaksEaten")
        private int steaksEaten = 0;
        @Column(name = "ash_used", nullable = false)
        @SerializedName("ashUsed")
        private int ashUsed = 0;
        @Column(name = "divination_used", nullable = false)
        @SerializedName("divinationUsed")
        private int divinationUsed = 0;
        @Column(name = "invisibility_used", nullable = false)
        @SerializedName("invisibilityUsed")
        private int invisibilityUsed = 0;
        @Column(name = "swiftness_used", nullable = false)
        @SerializedName("swiftnessUsed")
        private int swiftnessUsed = 0;
        @Column(name = "revelation_used", nullable = false)
        @SerializedName("revelationUsed")
        private int revelationUsed = 0;
        @Column(name = "traitors_guide_used", nullable = false)
        @SerializedName("traitorsGuideUsed")
        private int traitorsGuideUsed = 0;

        //effectiveness stats
        @SerializedName("curseSpear")
        private CurseSpearStats curseSpear = new CurseSpearStats();

        @SerializedName("arrow")
        private ArrowStats arrow = new ArrowStats();

        @SerializedName("stunGrenade")
        private StunGrenadeStats stunGrenade = new StunGrenadeStats();

        @SerializedName("holyStar")
        private HolyStarStats holyStar = new HolyStarStats();

        @SerializedName("protection")
        private ProtectionStats protection = new ProtectionStats();

        @SerializedName("sneakNotice")
        private SneakNoticeStats sneakNotice = new SneakNoticeStats();

        @SerializedName("werewolfAxe")
        private WerewolfAxeStats werewolfAxe = new WerewolfAxeStats();

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

        public CurseSpearStats getCurseSpear() {
            return curseSpear;
        }

        public ArrowStats getArrow() {
            return arrow;
        }

        public StunGrenadeStats getStunGrenade() {
            return stunGrenade;
        }

        public HolyStarStats getHolyStar() {
            return holyStar;
        }

        public ProtectionStats getProtection() {
            return protection;
        }

        public SneakNoticeStats getSneakNotice() {
            return sneakNotice;
        }

        public WerewolfAxeStats getWerewolfAxe() {
            return werewolfAxe;
        }

        public void setSteaksEaten(int steaksEaten) {
            this.steaksEaten = steaksEaten;
        }

        public void setAshUsed(int ashUsed) {
            this.ashUsed = ashUsed;
        }

        public void setDivinationUsed(int divinationUsed) {
            this.divinationUsed = divinationUsed;
        }

        public void setInvisibilityUsed(int invisibilityUsed) {
            this.invisibilityUsed = invisibilityUsed;
        }

        public void setSwiftnessUsed(int swiftnessUsed) {
            this.swiftnessUsed = swiftnessUsed;
        }

        public void setRevelationUsed(int revelationUsed) {
            this.revelationUsed = revelationUsed;
        }

        public void setTraitorsGuideUsed(int traitorsGuideUsed) {
            this.traitorsGuideUsed = traitorsGuideUsed;
        }

        public void setCurseSpear(CurseSpearStats curseSpear) {
            this.curseSpear = curseSpear;
        }

        public void setArrow(ArrowStats arrow) {
            this.arrow = arrow;
        }

        public void setStunGrenade(StunGrenadeStats stunGrenade) {
            this.stunGrenade = stunGrenade;
        }

        public void setHolyStar(HolyStarStats holyStar) {
            this.holyStar = holyStar;
        }

        public void setProtection(ProtectionStats protection) {
            this.protection = protection;
        }

        public void setSneakNotice(SneakNoticeStats sneakNotice) {
            this.sneakNotice = sneakNotice;
        }

        public void setWerewolfAxe(WerewolfAxeStats werewolfAxe) {
            this.werewolfAxe = werewolfAxe;
        }

        @Embeddable
        public static final class CurseSpearStats {
            @SerializedName("melee")
            private MeleeStats melee = new MeleeStats();
            @SerializedName("thrown")
            private ThrownStats thrown = new ThrownStats();

            @Embeddable
            public static final class MeleeStats {
                @Column(name = "curse_spear_melee_used", nullable = false)
                @SerializedName("used")
                private int used = 0;
                @Column(name = "curse_spear_melee_curses", nullable = false)
                @SerializedName("cursed")
                private int cursed = 0;
                @Column(name = "curse_spear_melee_kills", nullable = false)
                @SerializedName("killed")
                private int killed = 0;

                public int getUsed() {
                    return used;
                }

                public void setUsed(int used) {
                    this.used = used;
                }

                public int getCursed() {
                    return cursed;
                }

                public void setCursed(int cursed) {
                    this.cursed = cursed;
                }

                public int getKilled() {
                    return killed;
                }

                public void setKilled(int killed) {
                    this.killed = killed;
                }
            }

            @Embeddable
            public static final class ThrownStats {
                @Column(name = "curse_spear_throw_used", nullable = false)
                @SerializedName("used")
                private int used = 0;
                @Column(name = "curse_spear_throw_hits", nullable = false)
                @SerializedName("hits")
                private int hit = 0;
                @Column(name = "curse_spear_throw_curses", nullable = false)
                @SerializedName("cursed")
                private int cursed = 0;
                @Column(name = "curse_spear_throw_kills", nullable = false)
                @SerializedName("killed")
                private int killed = 0;

                public int getUsed() {
                    return used;
                }

                public void setUsed(int used) {
                    this.used = used;
                }

                public int getHit() {
                    return hit;
                }

                public void setHit(int hit) {
                    this.hit = hit;
                }

                public int getCursed() {
                    return cursed;
                }

                public void setCursed(int cursed) {
                    this.cursed = cursed;
                }

                public int getKilled() {
                    return killed;
                }

                public void setKilled(int killed) {
                    this.killed = killed;
                }
            }

            public MeleeStats getMelee() {
                return melee;
            }

            public void setMelee(MeleeStats melee) {
                this.melee = melee;
            }

            public ThrownStats getThrown() {
                return thrown;
            }

            public void setThrown(ThrownStats thrown) {
                this.thrown = thrown;
            }
        }

        @Embeddable
        public static final class ArrowStats {
            @Column(name = "arrow_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "arrow_hits", nullable = false)
            @SerializedName("hits")
            private int hit = 0;
            @Column(name = "arrow_kills", nullable = false)
            @SerializedName("kills")
            private int killed = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getHit() {
                return hit;
            }

            public void setHit(int hit) {
                this.hit = hit;
            }

            public int getKilled() {
                return killed;
            }

            public void setKilled(int killed) {
                this.killed = killed;
            }
        }

        @Embeddable
        public static final class StunGrenadeStats {
            @Column(name = "stun_grenade_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "stun_grenade_hits", nullable = false)
            @SerializedName("hits")
            private int hit = 0;
            @Column(name = "stun_grenade_hit_targets", nullable = false)
            @SerializedName("hitTargets")
            private int affected_players = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getHit() {
                return hit;
            }

            public void setHit(int hit) {
                this.hit = hit;
            }

            public int getAffected_players() {
                return affected_players;
            }

            public void setAffected_players(int affected_players) {
                this.affected_players = affected_players;
            }
        }

        @Embeddable
        public static final class HolyStarStats {
            @Column(name = "holy_star_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "holy_star_killed", nullable = false)
            @SerializedName("killed")
            private int killed = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getKilled() {
                return killed;
            }

            public void setKilled(int killed) {
                this.killed = killed;
            }
        }

        @Embeddable
        public static final class ProtectionStats {
            @Column(name = "protection_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "protection_activated", nullable = false)
            @SerializedName("activated")
            private int activated = 0;
            @Column(name = "protection_triggered", nullable = false)
            @SerializedName("triggered")
            private int triggered = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getActivated() {
                return activated;
            }

            public void setActivated(int activated) {
                this.activated = activated;
            }

            public int getTriggered() {
                return triggered;
            }

            public void setTriggered(int triggered) {
                this.triggered = triggered;
            }
        }

        @Embeddable
        public static final class SneakNoticeStats {
            @Column(name = "sneak_notice_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "sneak_notice_trigerred", nullable = false)
            @SerializedName("triggered")
            private int triggered = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getTriggered() {
                return triggered;
            }

            public void setTriggered(int triggered) {
                this.triggered = triggered;
            }
        }

        @Embeddable
        public static final class WerewolfAxeStats {
            @Column(name = "werewolf_axe_used", nullable = false)
            @SerializedName("used")
            private int used = 0;
            @Column(name = "werewolf_axe_kills", nullable = false)
            @SerializedName("killed")
            private int killed = 0;

            public int getUsed() {
                return used;
            }

            public void setUsed(int used) {
                this.used = used;
            }

            public int getKilled() {
                return killed;
            }

            public void setKilled(int killed) {
                this.killed = killed;
            }
        }
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
        this.itemStats.steaksEaten += 1;
    }
    
    public void addAsheUsed() {
        this.itemStats.ashUsed += 1;
    }
    
    public void addDivinationUsed() {
        this.itemStats.divinationUsed += 1;
    }
    
    public void addInvisibilityUsed() {
        this.itemStats.invisibilityUsed += 1;
    }
    
    public void addSwiftnessUsed() {
        this.itemStats.swiftnessUsed += 1;
    }
    
    public void addRevelationUsed() {
        this.itemStats.revelationUsed += 1;
    }
    
    public void addTraitorsGuideUsed() {
        this.itemStats.traitorsGuideUsed += 1;
    }
    
    public void addCurseSpearMeleeUsed(boolean cursed, boolean killed) {
        this.itemStats.curseSpear.melee.used += 1;
        if (cursed) this.itemStats.curseSpear.melee.cursed += 1;
        if (killed) this.itemStats.curseSpear.melee.killed += 1;
    }
    
    public void addCurseSpearThrowUsed() {
        this.itemStats.curseSpear.thrown.used += 1;
    }
    
    public void addCurseSpearThrowHits(boolean cursed, boolean killed) {
        this.itemStats.curseSpear.thrown.hit += 1;
        if (cursed) this.itemStats.curseSpear.thrown.cursed += 1;
        if (killed) this.itemStats.curseSpear.thrown.killed += 1;
    }

    public void addArrowUsed() {
        this.itemStats.arrow.used += 1;
    }

    public void addArrowHits(boolean killed) {
        this.itemStats.arrow.hit += 1;
        if (killed) this.itemStats.arrow.killed += 1;
    }

    public void addStunGrenadeUsed() {
        this.itemStats.stunGrenade.used += 1;
    }
    
    public void addStunGrenadeHits(int targets) {
        this.itemStats.stunGrenade.hit += 1;
        this.itemStats.stunGrenade.affected_players += targets;
    }

    public void addHolyStarUsed(boolean killed) {
        this.itemStats.holyStar.used += 1;
        if (killed) this.itemStats.holyStar.killed += 1;
    }
    
    public void addProtectionUsed(boolean activated) {
        this.itemStats.protection.used += 1;
        if (activated) this.itemStats.protection.activated += 1;
    }
    
    public void addProtectionTriggered() {
        this.itemStats.protection.triggered += 1;
    }

    public void addSneakNoticeUsed() {
        this.itemStats.sneakNotice.used += 1;
    }
    
    public void addSneakNoticeTriggered() {
        this.itemStats.sneakNotice.triggered += 1;
    }

    public void addWerewolfAxeUsed(boolean killed) {
        this.itemStats.werewolfAxe.used += 1;
        if (killed) this.itemStats.werewolfAxe.killed += 1;
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

    public ItemStats getItemStats() {
        return itemStats;
    }


}
