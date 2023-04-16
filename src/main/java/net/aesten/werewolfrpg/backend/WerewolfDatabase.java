package net.aesten.werewolfrpg.backend;

import net.aesten.werewolfrpg.WerewolfRpg;
import org.h2.tools.RunScript;

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
            openConnection();
            InputStream script = getClass().getClassLoader().getResourceAsStream("init.sql");
            if (script == null) {
                WerewolfRpg.logConsole("Failed to load initialization script for h2 database");
            } else {
                RunScript.execute(conn, new InputStreamReader(script));
            }
            closeConnection();
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to run SQL init script on h2 database");
            throw new RuntimeException(sqlException);
        }
    }

    public void openConnection() {
        try {
            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException sqlException) {
            WerewolfRpg.logConsole("Failed to open SQL connection to h2 database");
            throw new RuntimeException(sqlException);
        } catch (ClassNotFoundException classNotFoundException) {
            WerewolfRpg.logConsole("Failed to load h2 database drivers: 'org.h2.Driver'");
            throw new RuntimeException(classNotFoundException);
        }
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                conn = null;
            } catch (SQLException sqlException) {
                WerewolfRpg.logConsole("Failed to close SQL connection to h2 database");
                throw new RuntimeException(sqlException);
            }
        }
    }

    public List<String> query(String query) throws SQLException {
        List<String> results = new ArrayList<>();
        openConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            results.add(rs.getString(1));
        }
        rs.close();
        stmt.close();
        closeConnection();
        return results;
    }

    public void execute(String query) throws SQLException {
        openConnection();
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.executeUpdate();
        stmt.close();
        closeConnection();
    }
}
