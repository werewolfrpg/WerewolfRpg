package net.aesten.werewolfrpg.backend;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.backend.controllers.MatchRecordController;
import net.aesten.werewolfrpg.backend.controllers.PlayerDataController;
import net.aesten.werewolfrpg.backend.controllers.PlayerStatsController;
import net.aesten.werewolfrpg.backend.models.MatchRecord;
import net.aesten.werewolfrpg.backend.models.PlayerData;
import net.aesten.werewolfrpg.backend.models.PlayerStats;
import net.aesten.werewolfrpg.plugin.utilities.WerewolfUtil;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import javax.sql.DataSource;
import java.util.Map;
import java.util.UUID;

public class WerewolfBackend {
    private static final Map<String, String> DIALECT_MAPPING = Map.of(
            "com.mysql.cj.jdbc.Driver", "org.hibernate.dialect.MySQLDialect",
            "org.postgresql.Driver", "org.hibernate.dialect.PostgreSQLDialect",
            "org.sqlite.JDBC", "org.hibernate.dialect.SQLiteDialect",
            "com.microsoft.sqlserver.jdbc.SQLServerDriver", "org.hibernate.dialect.SQLServerDialect",
            "org.h2.Driver", "org.hibernate.dialect.H2Dialect"
    );

    private static WerewolfBackend backend;
    private final static BackendConfig config = new BackendConfig();
    private final Javalin app;
    private final PlayerDataController pdc;
    private final MatchRecordController mrc;
    private final PlayerStatsController psc;
    private UUID token;

    private WerewolfBackend() {
        HikariConfig dbConfig = new HikariConfig();
        dbConfig.setJdbcUrl(config.getJdbcUrl().get());
        dbConfig.setDriverClassName(config.getJdbcDriver().get());
        dbConfig.setUsername(config.getJdbcUsername().get());
        dbConfig.setPassword(config.getJdbcPassword().get());

        DataSource dataSource = new HikariDataSource(dbConfig);

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .applySetting("hibernate.connection.datasource", dataSource)
                .applySetting("hibernate.dialect", DIALECT_MAPPING.get(config.getJdbcDriver().get()))
                .applySetting("hibernate.show_sql", config.getBackendShowSql().get())
                .applySetting("hibernate.format_sql", config.getBackendShowSql().get())
                .applySetting("hibernate.hbm2ddl.auto", "update")
                .build();

        Metadata metadata = new MetadataSources(registry)
                .addAnnotatedClass(PlayerData.class)
                .addAnnotatedClass(MatchRecord.class)
                .addAnnotatedClass(PlayerStats.class)
                .buildMetadata();

        SessionFactory sessionFactory = metadata.buildSessionFactory();

        pdc = new PlayerDataController(sessionFactory);
        mrc = new MatchRecordController(sessionFactory);
        psc = new PlayerStatsController(sessionFactory);

        app = Javalin.create(jConfig -> jConfig.plugins.enableCors(cors -> cors.add(it -> config.getBackendCorsAllowed().get().forEach(it::allowHost))));

        app.before("/api/admin/*", ctx -> {
            String authHeader = ctx.header("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                ctx.status(401).result("Unauthorized");
                return;
            }

            if (!isAdminUser(authHeader.substring("Bearer ".length()).trim())) {
                ctx.status(403).result("Forbidden");
            }
        });

        app.post("/api/admin/player", pdc::apiRegisterPlayer); //add a new player
        app.delete("/api/admin/player/discord_id/{discord_id}", pdc::apiDeletePlayerByDiscordId); //delete the player with corresponding Discord id
        app.put("/api/admin/player/minecraft_id/{minecraft_id}/score/{score}/set", pdc::apiSetScoreOfPlayer); //set the score of a player
        app.put("/api/admin/player/minecraft_id/{minecraft_id}/score/{score}/add", pdc::apiAddScoreToPlayer); //add score to a player
        app.get("/api/player/minecraft_id/{minecraft_id}/score", pdc::apiGetScoreOfPlayer); //get the score of a player
        app.get("/api/player/minecraft_id/{minecraft_id}/discord_id", pdc::apiGetDiscordIdOfPlayer); //get Discord id of Minecraft player
        app.get("/api/player/discord_id/{discord_id}/minecraft_id", pdc::apiGetMinecraftIdFromDiscordId); //get Minecraft id of Discord user
        app.get("/api/players", pdc::apiGetAllPlayerData); //get all registered players
        app.get("/api/players/minecraft_id", pdc::apiGetAllMinecraftIds); //get all registered Minecraft ids
        app.get("/api/players/discord_id", pdc::apiGetAllDiscordIds); //get all registered Discord ids

        app.post("/api/admin/match", mrc::apiRecordMatch); //add a new match record
        app.delete("/api/admin/match/{match_id}", mrc::apiDeleteMatch); //delete a match record (along with associated player stats record)
        app.get("/api/matches", mrc::apiGetAllMatches); //get all registered matches

        app.post("/api/admin/stats", psc::apiSavePlayerStats); //add stats record of a player for a match
        app.delete("/api/admin/stats/{match_id}", psc::apiDeleteStatsByMatchId); //delete all records of corresponding match_id
        app.delete("/api/admin/stats/{minecraft_id}", psc::apiDeleteStatsByPlayerId); //delete all records of corresponding player id
        app.delete("/api/admin/stats/{match_id}/{minecraft_id}", psc::apiDeleteStatsByPlayerIdAndMatchId); //delete a specific record of match regarding a player
        app.get("/api/stats", psc::apiGetAllStats); //get all registered stats

        app.start(config.getBackendPort().get());

        regenToken();
    }

    public Javalin getApp() {
        return app;
    }

    private static boolean checkConfig() {
        return false;
    }

    public static void init() {
        WerewolfRpg.logConsole("Enabling Javalin backend");
        AzaleaConfigurationApi.load(WerewolfRpg.getPlugin(), config);
        if (checkConfig()) {
            backend = null;
            WerewolfRpg.logConsole("Javalin backend isn't configured, not enabling");
        } else {
            try {
                backend = new WerewolfBackend();
                WerewolfRpg.logConsole("Javalin backend has been enabled");
            } catch (Exception e) {
                WerewolfRpg.logConsole("Could not enable backend");
                throw new RuntimeException(e);
            }
        }
    }

    public static void shutDown() {
        if (backend != null) {
            WerewolfRpg.logConsole("Shutting down Javalin backend");
            backend.getApp().close();
            AzaleaConfigurationApi.save(WerewolfRpg.getPlugin(), config);
        }
    }

    private boolean isAdminUser(String token) {
        UUID tokenId;
        try {
            tokenId = UUID.fromString(token);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this.token.equals(tokenId);
    }

    private void regenToken() {
        token = UUID.randomUUID();
        WerewolfRpg.logConsole("Admin token: " + token + " (valid " + config.getBackendTokenValidity().get() + " hours)");
        WerewolfUtil.runDelayedTask(20 * 3600 * config.getBackendTokenValidity().get(), this::regenToken);
    }

    public static WerewolfBackend getBackend() {
        return backend;
    }

    public PlayerDataController getPdc() {
        return pdc;
    }

    public MatchRecordController getMrc() {
        return mrc;
    }

    public PlayerStatsController getPsc() {
        return psc;
    }
}
