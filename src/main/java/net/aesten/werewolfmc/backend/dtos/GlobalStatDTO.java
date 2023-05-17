package net.aesten.werewolfmc.backend.dtos;

import com.google.gson.annotations.SerializedName;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.statistics.Rank;
import net.aesten.werewolfmc.plugin.statistics.Result;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GlobalStatDTO {
    @SerializedName("minecraftId")
    private UUID playerID;
    @SerializedName("minecraftUsername")
    private String username;
    @SerializedName("score")
    private int score;
    @SerializedName("ranking")
    private int ranking;
    @SerializedName("title")
    private Rank title;
    @SerializedName("nextTitle")
    private Rank nextTitle;
    @SerializedName("scoreOverCurrentTitle")
    private int scoreOverCurrentRank;
    @SerializedName("scoreUntilNextTileMax")
    private int rankScoreDiff;
    @SerializedName("kills")
    private int kills;
    @SerializedName("deaths")
    private int deaths;
    @SerializedName("gameStats")
    private List<GameStats> gameStats;
    @SerializedName("skeletons")
    private PlayerStats.SkeletonStats skeletons;
    @SerializedName("items")
    private PlayerStats.ItemStats items;

    private static final class GameStats {
        @SerializedName("role")
        private Role role;
        @SerializedName("data")
        private RoleStats stats;

        public GameStats(Role role, RoleStats stats) {
            this.role = role;
            this.stats = stats;
        }

        private static final class RoleStats {
            @SerializedName("played")
            private int played;
            @SerializedName("victories")
            private int victories;

            public RoleStats(int played, int victories) {
                this.played = played;
                this.victories = victories;
            }

            public int getPlayed() {
                return played;
            }

            public void setPlayed(int played) {
                this.played = played;
            }

            public int getVictories() {
                return victories;
            }

            public void setVictories(int victories) {
                this.victories = victories;
            }
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public RoleStats getStats() {
            return stats;
        }

        public void setStats(RoleStats stats) {
            this.stats = stats;
        }
    }

    public static GlobalStatDTO computeGlobalStats(List<PlayerStats> stats) {
        if (stats.size() == 0) return null;

        GlobalStatDTO globalStats = new GlobalStatDTO();

        globalStats.playerID = stats.get(0).getPlayerId();

        PlayerData playerData = WerewolfBackend.getBackend().getPdc().getPlayerDataOfPlayer(globalStats.playerID).join();

        globalStats.username = playerData.getMcName();
        globalStats.score = playerData.getScore();
        globalStats.ranking = WerewolfBackend.getBackend().getPdc().getPlayerRanking(globalStats.playerID).join();
        globalStats.title = WerewolfGame.getScoreManager().getScoreRank(globalStats.score);
        globalStats.nextTitle = WerewolfGame.getScoreManager().getNextKey(globalStats.title);
        int currentRankThreshold = WerewolfGame.getScoreManager().getScoreThresholdOfRank(globalStats.title);
        globalStats.scoreOverCurrentRank = globalStats.score - currentRankThreshold;
        globalStats.rankScoreDiff = globalStats.nextTitle != null ? WerewolfGame.getScoreManager().getScoreThresholdOfRank(globalStats.nextTitle) - currentRankThreshold : 0;
        globalStats.kills = stats.stream().mapToInt(PlayerStats::getKills).sum();
        globalStats.deaths = (int) stats.stream().filter(s -> s.getKillerId() != null).count();

        //game stats
        List<GameStats> gameStatsList = new ArrayList<>();
        List<PlayerStats> filteredData = stats.stream().filter(s -> s.getResult() != Result.DISCONNECTED && s.getResult() != Result.CANCELLED).toList();

        List<PlayerStats> villagerData = filteredData.stream().filter(s -> s.getRole() == Role.VILLAGER).toList();
        int villagerVictories = (int) villagerData.stream().filter(s -> s.getResult() == Result.VICTORY).count();
        gameStatsList.add(new GameStats(Role.VILLAGER, new GameStats.RoleStats(villagerData.size(), villagerVictories)));

        List<PlayerStats> werewolfData = filteredData.stream().filter(s -> s.getRole() == Role.WEREWOLF).toList();
        int werewolfVictories = (int) werewolfData.stream().filter(s -> s.getResult() == Result.VICTORY).count();
        gameStatsList.add(new GameStats(Role.WEREWOLF, new GameStats.RoleStats(werewolfData.size(), werewolfVictories)));

        List<PlayerStats> traitorData = filteredData.stream().filter(s -> s.getRole() == Role.TRAITOR).toList();
        int traitorVictories = (int) traitorData.stream().filter(s -> s.getResult() == Result.VICTORY).count();
        gameStatsList.add(new GameStats(Role.TRAITOR, new GameStats.RoleStats(traitorData.size(), traitorVictories)));

        List<PlayerStats> vampireData = filteredData.stream().filter(s -> s.getRole() == Role.VAMPIRE).toList();
        int vampireVictories = (int) vampireData.stream().filter(s -> s.getResult() == Result.VICTORY).count();
        gameStatsList.add(new GameStats(Role.VAMPIRE, new GameStats.RoleStats(vampireData.size(), vampireVictories)));

        List<PlayerStats> possessedData = filteredData.stream().filter(s -> s.getRole() == Role.POSSESSED).toList();
        int possessedVictories = (int) possessedData.stream().filter(s -> s.getResult() == Result.VICTORY).count();
        gameStatsList.add(new GameStats(Role.POSSESSED, new GameStats.RoleStats(possessedData.size(), possessedVictories)));

        globalStats.gameStats = gameStatsList;

        //skeleton stats
        PlayerStats.SkeletonStats skeletonStats = new PlayerStats.SkeletonStats();
        skeletonStats.setKilledBasicSkeletons(stats.stream().mapToInt(s -> s.getSkeletonStats().getKilledBasicSkeletons()).sum());
        skeletonStats.setKilledLuckySkeletons(stats.stream().mapToInt(s -> s.getSkeletonStats().getKilledLuckySkeletons()).sum());
        skeletonStats.setKilledSpecialSkeletons(stats.stream().mapToInt(s -> s.getSkeletonStats().getKilledSpecialSkeletons()).sum());
        skeletonStats.setBasicSkeletonEmeraldDrops(stats.stream().mapToInt(s -> s.getSkeletonStats().getBasicSkeletonEmeraldDrops()).sum());
        globalStats.skeletons = skeletonStats;

        //item stats
        PlayerStats.ItemStats itemStats = new PlayerStats.ItemStats();
        itemStats.setSteaksEaten(stats.stream().mapToInt(s -> s.getItemStats().getSteaksEaten()).sum());
        itemStats.setAshUsed(stats.stream().mapToInt(s -> s.getItemStats().getAshUsed()).sum());
        itemStats.setDivinationUsed(stats.stream().mapToInt(s -> s.getItemStats().getDivinationUsed()).sum());
        itemStats.setInvisibilityUsed(stats.stream().mapToInt(s -> s.getItemStats().getInvisibilityUsed()).sum());
        itemStats.setSwiftnessUsed(stats.stream().mapToInt(s -> s.getItemStats().getSwiftnessUsed()).sum());
        itemStats.setRevelationUsed(stats.stream().mapToInt(s -> s.getItemStats().getRevelationUsed()).sum());
        itemStats.setTraitorsGuideUsed(stats.stream().mapToInt(s -> s.getItemStats().getTraitorsGuideUsed()).sum());

        itemStats.getCurseSpear().getMelee().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getMelee().getUsed()).sum());
        itemStats.getCurseSpear().getMelee().setCursed(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getMelee().getCursed()).sum());
        itemStats.getCurseSpear().getMelee().setKilled(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getMelee().getKilled()).sum());
        itemStats.getCurseSpear().getThrown().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getThrown().getUsed()).sum());
        itemStats.getCurseSpear().getThrown().setHit(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getThrown().getHit()).sum());
        itemStats.getCurseSpear().getThrown().setCursed(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getThrown().getCursed()).sum());
        itemStats.getCurseSpear().getThrown().setKilled(stats.stream().mapToInt(s -> s.getItemStats().getCurseSpear().getThrown().getKilled()).sum());

        itemStats.getArrow().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getArrow().getUsed()).sum());
        itemStats.getArrow().setHit(stats.stream().mapToInt(s -> s.getItemStats().getArrow().getHit()).sum());
        itemStats.getArrow().setKilled(stats.stream().mapToInt(s -> s.getItemStats().getArrow().getKilled()).sum());

        itemStats.getStunGrenade().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getStunGrenade().getUsed()).sum());
        itemStats.getStunGrenade().setHit(stats.stream().mapToInt(s -> s.getItemStats().getStunGrenade().getHit()).sum());
        itemStats.getStunGrenade().setAffected_players(stats.stream().mapToInt(s -> s.getItemStats().getStunGrenade().getAffected_players()).sum());

        itemStats.getHolyStar().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getHolyStar().getUsed()).sum());
        itemStats.getHolyStar().setKilled(stats.stream().mapToInt(s -> s.getItemStats().getHolyStar().getKilled()).sum());

        itemStats.getProtection().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getProtection().getUsed()).sum());
        itemStats.getProtection().setActivated(stats.stream().mapToInt(s -> s.getItemStats().getProtection().getActivated()).sum());
        itemStats.getProtection().setTriggered(stats.stream().mapToInt(s -> s.getItemStats().getProtection().getTriggered()).sum());

        itemStats.getSneakNotice().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getSneakNotice().getUsed()).sum());
        itemStats.getSneakNotice().setTriggered(stats.stream().mapToInt(s -> s.getItemStats().getSneakNotice().getTriggered()).sum());

        itemStats.getWerewolfAxe().setUsed(stats.stream().mapToInt(s -> s.getItemStats().getWerewolfAxe().getUsed()).sum());
        itemStats.getWerewolfAxe().setKilled(stats.stream().mapToInt(s -> s.getItemStats().getWerewolfAxe().getKilled()).sum());

        globalStats.items = itemStats;

        return globalStats;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public void setPlayerID(UUID playerID) {
        this.playerID = playerID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public Rank getTitle() {
        return title;
    }

    public void setTitle(Rank title) {
        this.title = title;
    }

    public Rank getNextTitle() {
        return nextTitle;
    }

    public void setNextTitle(Rank nextTitle) {
        this.nextTitle = nextTitle;
    }

    public int getScoreOverCurrentRank() {
        return scoreOverCurrentRank;
    }

    public void setScoreOverCurrentRank(int scoreOverCurrentRank) {
        this.scoreOverCurrentRank = scoreOverCurrentRank;
    }

    public int getRankScoreDiff() {
        return rankScoreDiff;
    }

    public void setRankScoreDiff(int rankScoreDiff) {
        this.rankScoreDiff = rankScoreDiff;
    }

    public int getKills() {
        return kills;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public List<GameStats> getGameStats() {
        return gameStats;
    }

    public void setGameStats(List<GameStats> gameStats) {
        this.gameStats = gameStats;
    }

    public PlayerStats.SkeletonStats getSkeletons() {
        return skeletons;
    }

    public void setSkeletons(PlayerStats.SkeletonStats skeletons) {
        this.skeletons = skeletons;
    }

    public PlayerStats.ItemStats getItems() {
        return items;
    }

    public void setItems(PlayerStats.ItemStats items) {
        this.items = items;
    }
}
