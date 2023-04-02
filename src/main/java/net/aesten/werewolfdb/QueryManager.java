package net.aesten.werewolfdb;

import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.tracker.PlayerStats;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

public class QueryManager {
    public static List<String> requestGuildIdList() {
        String sql = "SELECT GUILDID FROM GUILDS";
        try {
            return WerewolfDatabase.getInstance().query(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to fetch list of guild ids failed");
            throw new RuntimeException(e);
        }
    }

    public static String requestGuildVoiceChannel(String guildId) {
        String sql = "SELECT VCID FROM GUILDS WHERE GUILID=" + guildId;
        try {
            return getResult(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to get vc channel from guild failed");
            throw new RuntimeException(e);
        }
    }

    public static String requestGuildLogChannel(String guildId) {
        String sql = "SELECT LOGCHID FROM GUILDS WHERE GUILID=" + guildId;
        try {
            return getResult(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to get log channel from guild failed");
            throw new RuntimeException(e);
        }
    }

    public static void removeGuild(String guildId) {
        String sql = "DELETE FROM GUILDS WHERE GUILID=" + guildId;
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to remove a guild failed");
            throw new RuntimeException(e);
        }
    }

    public static void addGuild(String guildId, String vcId, String lcId) {
        String sql = "INSERT INTO GUILDS VALUES (" + guildId + "," + vcId + "," + lcId + ")";
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to add a guild failed");
            throw new RuntimeException(e);
        }
    }

    public static List<String> getDiscordIdsOfPlayer(String mcId) {
        String sql = "SELECT DCID FROM IDBINDINGS WHERE MCID='" + mcId + "'";
        try {
            return WerewolfDatabase.getInstance().query(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to get Discord ids from Minecraft id failed");
            throw new RuntimeException(e);
        }
    }

    public static String getMcIdOfDiscordUser(String dcId) {
        String sql = "SELECT MCID FROM IDBINDINGS WHERE DCID=" + dcId;
        try {
            return getResult(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to get Minecraft id from Discord id failed");
            throw new RuntimeException(e);
        }
    }

    public static List<String> getAllMcIds() {
        String sql = "SELECT MCID FROM IDBINDINGS";
        try {
            return WerewolfDatabase.getInstance().query(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to get all registered Minecraft ids failed");
            throw new RuntimeException(e);
        }
    }

    public static void addIdBinding(String mcId, String dcId) {
        String sql = "INSERT INTO IDBINDINGS (MCID, DCID) VALUES ('" + mcId + "'," + dcId + ")";
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to register a Discord id to Minecraft id binding failed");
            throw new RuntimeException(e);
        }
    }

    public static void removeBinding(String dcId) {
        String sql = "DELETE FROM IDBINDINGS WHERE DCID=" + dcId;
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to unregister an id binding failed failed");
            throw new RuntimeException(e);
        }
    }

    public static void addMatchRecord(String matchId, Timestamp start, Timestamp end, Role role) {
        String sql;
        if (role == null) {
            sql = "INSERT INTO MATCHES (MATCH_ID, START_TIME, END_TIME) VALUES ('" +
                    matchId + "','" +
                    start + "','" +
                    end + "')";
        } else {
            sql = "INSERT INTO MATCHES ('" +
                    matchId + "','" +
                    start + "','" +
                    end + "','" +
                    role.name + "')";
        }
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to record match failed");
            throw new RuntimeException(e);
        }
    }

    public static void addPlayerMatchRecord(String matchId, PlayerStats stats) {
        String sql = "INSERT INTO RECORDS VALUES ('" +
                matchId + "','" +
                stats.getPlayerId() + "','" +
                stats.getRole().toString() + "','" +
                stats.getResult().toString() + "'," +
                stats.getKills() + ",'" +
                stats.getKillerId() + "','" +
                stats.getDeathCause() + "'," +
                stats.getKilledBasicSkeletons() + "," +
                stats.getKilledLuckySkeletons() + "," +
                stats.getKilledSpecialSkeletons() + "," +
                stats.getBasicSkeletonEmeraldDrops() + "," +
                stats.getSteaksEaten() + "," +
                stats.getAsheUsed() + "," +
                stats.getDivinationUsed() + "," +
                stats.getInvisibilityUsed() + "," +
                stats.getSwiftnessUsed() + "," +
                stats.getRevelationUsed() + "," +
                stats.getTraitorsGuideUsed() + "," +
                stats.getCurseSpearMeleeUsed() + "," +
                stats.getCurseSpearMeleeCurses() + "," +
                stats.getCurseSpearMeleeKills() + "," +
                stats.getCurseSpearThrowUsed() + "," +
                stats.getCurseSpearThrowHits() + "," +
                stats.getCurseSpearThrowCurses() + "," +
                stats.getCurseSpearThrowKills() + "," +
                stats.getArrowUsed() + "," +
                stats.getArrowHits() + "," +
                stats.getArrowKills() + "," +
                stats.getStunGrenadeUsed() + "," +
                stats.getStunGrenadeHits() + "," +
                stats.getStunGrenadeHitTargets() + "," +
                stats.getHolyStarUsed() + "," +
                stats.getHolyStarKills() + "," +
                stats.getProtectionUsed() + "," +
                stats.getProtectionActivated() + "," +
                stats.getProtectionTriggered() + "," +
                stats.getSneakNoticeUsed() + "," +
                stats.getSneakNoticeTriggered() + "," +
                stats.getWerewolfAxeUsed() + "," +
                stats.getWerewolfAxeKills() + "," +
                ")";
        try {
            WerewolfDatabase.getInstance().execute(sql);
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Query to insert match statistics failed");
            throw new RuntimeException(e);
        }
    }

    private static String getResult(String sql) throws SQLException {
        List<String> result;
        result = WerewolfDatabase.getInstance().query(sql);
        if (result.size() > 0) return result.get(0);
        else return "";
    }
}
