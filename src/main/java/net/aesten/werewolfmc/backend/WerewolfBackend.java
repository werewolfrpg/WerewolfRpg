package net.aesten.werewolfmc.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.controllers.LeaderboardController;
import net.aesten.werewolfmc.backend.controllers.MatchRecordController;
import net.aesten.werewolfmc.backend.controllers.PlayerDataController;
import net.aesten.werewolfmc.backend.controllers.PlayerStatsController;
import net.aesten.werewolfmc.backend.models.MatchRecord;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.jetbrains.annotations.NotNull;

import javax.sql.DataSource;
import java.lang.reflect.Type;
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
    private final LeaderboardController lbc;
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

        Gson gson = new GsonBuilder().create();
        JsonMapper gsonMapper = new JsonMapper() {
            @Override
            public @NotNull String toJsonString(@NotNull Object obj, @NotNull Type type) {
                return gson.toJson(obj, type);
            }

            @Override
            public <T> @NotNull T fromJsonString(@NotNull String json, @NotNull Type targetType) {
                return gson.fromJson(json, targetType);
            }
        };

        pdc = new PlayerDataController(sessionFactory);
        mrc = new MatchRecordController(sessionFactory);
        psc = new PlayerStatsController(sessionFactory);
        lbc = new LeaderboardController(sessionFactory);

        if (config.getBackendCorsEnabled().get() && !config.getBackendCorsAllowedHosts().get().isEmpty()) {
            app = Javalin.create(jConfig -> {
                jConfig.plugins.enableCors(cors -> cors.add(it -> config.getBackendCorsAllowedHosts().get().forEach(it::allowHost)));
                jConfig.jsonMapper(gsonMapper);
            });
        } else {
            app = Javalin.create(jConfig -> jConfig.jsonMapper(gsonMapper));
        }


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

        app.post("/api/admin/player", pdc::apiRegisterPlayer);
        app.put("/api/admin/player/{discord_id}", pdc::apiUpdatePlayer);
        app.delete("/api/admin/player/discord_id/{discord_id}", pdc::apiDeletePlayerByDiscordId);
        app.get("/api/player/{discord_id}", pdc::apiGetPlayerFromDiscordId);
        app.get("/api/player/{minecraft_id}", pdc::apiGetPlayerFromMinecraftId);
        app.get("/api/players", pdc::apiGetAllPlayerData);

        app.post("/api/admin/match", mrc::apiRecordMatch);
        app.put("/api/admin/match/{match_id}", mrc::apiUpdateMatchRecord);
        app.delete("/api/admin/match/{match_id}", mrc::apiDeleteMatch);
        app.get("/api/match_history", mrc::apiGetAllMatches);
        app.get("/api/match/{match_id}", mrc::apiGetRecordsOfMatch);
        app.get("/api/match/{minecraft_id}", mrc::apiGetMatchHistoryOfPlayer);

        app.post("/api/admin/stats", psc::apiSavePlayerStats);
        app.put("/api/admin/stats/{id}", psc::apiUpdateStats);
        app.delete("/api/admin/stats/{match_id}", psc::apiDeleteStatsByMatchId);
        app.delete("/api/admin/stats/{minecraft_id}", psc::apiDeleteStatsByPlayerId);
        app.delete("/api/admin/stats/{id}", psc::apiDeleteStatsByPlayerIdAndMatchId);
        app.get("/api/stats", psc::apiGetAllStats);
        app.get("/api/stats/{minecraft_id}", psc::apiGetGlobalStatsOfPlayer);

        app.get("/api/leaderboard/{page}/{number}", lbc::apiGetPlayerIds);

        new Thread(() -> app.start(config.getBackendPort().get())).start();

        regenToken();
    }

    public Javalin getApp() {
        return app;
    }

    public static void init() {
        WerewolfPlugin.logConsole("Enabling Javalin backend");
        AzaleaConfigurationApi.load(WerewolfPlugin.getPlugin(), config);
        try {
            backend = new WerewolfBackend();
            WerewolfPlugin.logConsole("Javalin backend has been enabled");
        } catch (Exception e) {
            WerewolfPlugin.logConsole("Could not enable backend");
            backend = null;
            throw new RuntimeException(e);
        }
    }

    public static void shutDown() {
        if (backend != null) {
            WerewolfPlugin.logConsole("Shutting down Javalin backend");
            backend.getApp().close();
            AzaleaConfigurationApi.save(WerewolfPlugin.getPlugin(), config);
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
        WerewolfPlugin.logConsole("Admin token: " + token + " (valid " + config.getBackendTokenValidity().get() + " hours)");
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

    public LeaderboardController getLbc() {
        return lbc;
    }
}
