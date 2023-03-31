package net.aesten.werewolfdb;

import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.tracker.PlayerStats;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QueryManager {
    public static List<String> requestGuildIdList() {
        String sql = "SELECT GUILDID FROM GUILDS";
        return getResultList(sql);
    }

    public static String requestGuildVoiceChannel(String guildId) {
        String sql = "SELECT VCID FROM GUILDS WHERE GUILID=" + guildId;
        return getResult(sql);
    }

    public static String requestGuildLogChannel(String guildId) {
        String sql = "SELECT LOGCHID FROM GUILDS WHERE GUILID=" + guildId;
        return getResult(sql);
    }

    public static void removeGuild(String guildId) {
        String sql = "DELETE FROM GUILDS WHERE GUILID=" + guildId;
        try {
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }

    public static void addGuild(String guildId, String vcId, String lcId) {
        String sql = "INSERT INTO GUILDS VALUES (" + guildId + ", " + vcId + ", " + lcId + ")";
        try {
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }

    public static List<String> getDiscordIdsOfPlayer(String mcId) {
        String sql = "SELECT DCID FROM IDBINDINGS WHERE MCID=" + mcId;
        return getResultList(sql);
    }

    public static String getMcIdOfDiscordUser(String dcId) {
        String sql = "SELECT MCID FROM IDBINDINGS WHERE DCID=" + dcId;
        return getResult(sql);
    }

    public static void addIdBinding(String mcId, String dcId) {
        String sql = "INSERT INTO IDBINDINGS (DCID, MCID) VALUES (" + dcId + ", " + mcId + ")";
        try {
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }

    public static void removeBinding(String dcId) {
        String sql = "DELETE FROM IDBINDINGS WHERE DCID=" + dcId;
        try {
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }

    public static void addMatchRecord(String matchId, Timestamp start, Timestamp end, Role role) {
        String sql;
        if (role == null) {
            sql = "INSERT INTO MATCHES (MATCH_ID, START_TIME, END_TIME) VALUES (" +
                    matchId + "," +
                    start + "," +
                    end + ")";
        } else {
            sql = "INSERT INTO MATCHES (" +
                    matchId + "," +
                    start + "," +
                    end + "," +
                    role.name + ")";
        }
        try {
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }

    public static void addPlayerMatchRecord(String matchId, PlayerStats stats) {
        String sql = "INSERT INTO RECORDS VALUES (" +
                matchId + "," +
                stats.getPlayerId() + "," +
                stats.getRole().toString() + "," +
                stats.getResult().toString() + "," +
                stats.getKills() + "," +
                stats.getKillerId() + "," +
                stats.getDeathCause() + "," +
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
            WerewolfDatabase.getInstance().query(sql).close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to execute database query");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
    }


    private static List<String> getResultList(String sql) {
        List<String> list = new ArrayList<>();
        try {
            ResultSet rs = WerewolfDatabase.getInstance().query(sql);
            while (rs.next()) {
                list.add(rs.getString(1));
            }
            rs.close();
            return list;
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to read database query result");
            WerewolfRpg.logConsole("SQL: " + sql);
            return list;
        }
    }

    private static String getResult(String sql) {
        try {
            ResultSet rs = WerewolfDatabase.getInstance().query(sql);
            if (rs.next()) return rs.getString(1);
            rs.close();
        } catch (SQLException e) {
            WerewolfRpg.logConsole("Failed to read database query result");
            WerewolfRpg.logConsole("SQL: " + sql);
        }
        return "";
    }
}
