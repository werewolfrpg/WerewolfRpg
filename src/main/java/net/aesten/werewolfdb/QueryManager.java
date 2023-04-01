package net.aesten.werewolfdb;

import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.tracker.PlayerStats;

import java.sql.Timestamp;
import java.util.List;

public class QueryManager {
    public static List<String> requestGuildIdList() {
        String sql = "SELECT GUILDID FROM GUILDS";
        return WerewolfDatabase.getInstance().query(sql);
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
        WerewolfDatabase.getInstance().execute(sql);
    }

    public static void addGuild(String guildId, String vcId, String lcId) {
        String sql = "INSERT INTO GUILDS VALUES (" + guildId + "," + vcId + "," + lcId + ")";
        WerewolfDatabase.getInstance().execute(sql);
    }

    public static List<String> getDiscordIdsOfPlayer(String mcId) {
        String sql = "SELECT DCID FROM IDBINDINGS WHERE MCID='" + mcId + "'";
        return WerewolfDatabase.getInstance().query(sql);
    }

    public static String getMcIdOfDiscordUser(String dcId) {
        String sql = "SELECT MCID FROM IDBINDINGS WHERE DCID=" + dcId;
        return getResult(sql);
    }

    public static List<String> getAllMcIds() {
        String sql = "SELECT MCID FROM IDBINDINGS";
        return WerewolfDatabase.getInstance().query(sql);
    }

    public static void addIdBinding(String mcId, String dcId) {
        String sql = "INSERT INTO IDBINDINGS (DCID, MCID) VALUES (" + dcId + ",'" + mcId + "')";
        WerewolfDatabase.getInstance().execute(sql);
    }

    public static void removeBinding(String dcId) {
        String sql = "DELETE FROM IDBINDINGS WHERE DCID=" + dcId;
        WerewolfDatabase.getInstance().execute(sql);
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
        WerewolfDatabase.getInstance().execute(sql);
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
        WerewolfDatabase.getInstance().execute(sql);
    }

    private static String getResult(String sql) {
        List<String> result = WerewolfDatabase.getInstance().query(sql);
        if (result.size() > 0) return result.get(0);
        else return "";
    }
}
