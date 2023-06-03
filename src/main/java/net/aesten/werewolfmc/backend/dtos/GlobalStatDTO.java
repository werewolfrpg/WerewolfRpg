package net.aesten.werewolfmc.backend.dtos;

import com.google.gson.annotations.SerializedName;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.ItemStats;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.statistics.Rank;
import net.aesten.werewolfmc.plugin.statistics.Result;

import java.util.*;

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
    @SerializedName("currentThreshold")
    private int currentThreshold;
    @SerializedName("nextThreshold")
    private int nextThreshold;
    @SerializedName("kills")
    private int kills;
    @SerializedName("deaths")
    private int deaths;
    @SerializedName("gameStats")
    private List<GameStats> gameStats;
    @SerializedName("skeletons")
    private PlayerStats.SkeletonStats skeletons;
    @SerializedName("items")
    private List<ItemStats> items;

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
        globalStats.currentThreshold = WerewolfGame.getScoreManager().getScoreThresholdOfRank(globalStats.title);
        globalStats.nextThreshold = globalStats.nextTitle != null ? WerewolfGame.getScoreManager().getScoreThresholdOfRank(globalStats.nextTitle) : -1;
        globalStats.kills = stats.stream().mapToInt(PlayerStats::getKills).sum();
        globalStats.deaths = (int) stats.stream().filter(s -> s.getKillerId() != null).count();

        //game stats
        List<GameStats> gameStatsList = new ArrayList<>();
        List<PlayerStats> filteredData = stats.stream().filter(s -> s.getResult() != Result.CANCELLED).toList();

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
        globalStats.items = new ArrayList<>();

        globalStats.items.add(createItemStatsFor(stats, "Exquisite Meat"));
        globalStats.items.add(createItemStatsFor(stats, "Ash of the Dead"));
        globalStats.items.add(createItemStatsFor(stats, "Divination"));
        globalStats.items.add(createItemStatsFor(stats, "Invisibility Potion"));
        globalStats.items.add(createItemStatsFor(stats, "Swiftness Potion"));
        globalStats.items.add(createItemStatsFor(stats, "Revelation"));
        globalStats.items.add(createItemStatsFor(stats, "Traitor's Guide"));
        globalStats.items.add(createItemStatsFor(stats, "Curse Spear (Melee)"));
        globalStats.items.add(createItemStatsFor(stats, "Curse Spear (Thrown)"));
        globalStats.items.add(createItemStatsFor(stats, "Hunter's Bow"));
        globalStats.items.add(createItemStatsFor(stats, "Stun Grenade"));
        globalStats.items.add(createItemStatsFor(stats, "Holy Star"));
        globalStats.items.add(createItemStatsFor(stats, "Protection"));
        globalStats.items.add(createItemStatsFor(stats, "Sneak Notice"));
        globalStats.items.add(createItemStatsFor(stats, "Werewolf Axe"));
        globalStats.items.add(createItemStatsFor(stats, "Werewolf Trap"));

        return globalStats;
    }

    private static Map<String, Integer> sumMaps(List<Map<String, Integer>> maps) {
        Map<String, Integer> sumMap = new HashMap<>();

        for (Map<String, Integer> map : maps) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                int value = entry.getValue();

                sumMap.put(key, sumMap.getOrDefault(key, 0) + value);
            }
        }

        return sumMap;
    }

    private static ItemStats createItemStatsFor(List<PlayerStats> stats, String itemName) {
        return new ItemStats(itemName, sumMaps(stats.stream().map(ps -> ps.getByItemName(itemName)).map(ItemStats::getStats).toList()));
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

    public int getCurrentThreshold() {
        return currentThreshold;
    }

    public void setCurrentThreshold(int currentThreshold) {
        this.currentThreshold = currentThreshold;
    }

    public int getNextThreshold() {
        return nextThreshold;
    }

    public void setNextThreshold(int nextThreshold) {
        this.nextThreshold = nextThreshold;
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



}
