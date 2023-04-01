package net.aesten.werewolfdb;

import net.aesten.werewolfrpg.WerewolfRpg;
import org.h2.tools.RunScript;
import org.h2.tools.Server;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WerewolfDatabase {
    private static WerewolfDatabase INSTANCE;
    private static final String JDBC_DRIVER = "org.h2.Driver";
    private static final String DB_URL = "jdbc:h2:file:./plugins/WerewolfRPG/database/wwdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "wwrpgsa";
    private Connection conn;

    public static WerewolfDatabase getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WerewolfDatabase();
        }
        return INSTANCE;
    }

    public WerewolfDatabase() {
        try {
            InputStream script = getClass().getClassLoader().getResourceAsStream("init.sql");
            if (script == null) {
                WerewolfRpg.logConsole("Failed to load initialization script for h2 database");
            } else {
                RunScript.execute(conn, new InputStreamReader(script));
            }
            closeConnection();
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to run SQL init script on h2 database");
        }
    }

    public void openConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to open SQL connection to h2 database");
        } catch (ClassNotFoundException classNotFoundException) {
            WerewolfRpg.logConsole("Failed to load h2 database drivers: 'org.h2.Driver'");
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException sqlException) {
                WerewolfRpg.logConsole("Failed to close SQL connection to h2 database");
            }
        }
    }

    public List<String> query(String query) {
        List<String> results = new ArrayList<>();
        openConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                results.add(rs.getString(1));
            }
            rs.close();
            stmt.close();
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to query h2 database");
            sqlException.printStackTrace();
        }
        closeConnection();
        return results;
    }

    public void execute(String query) {
        openConnection();
        try {
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to query h2 database");
            sqlException.printStackTrace();
        }
        closeConnection();
    }
}
