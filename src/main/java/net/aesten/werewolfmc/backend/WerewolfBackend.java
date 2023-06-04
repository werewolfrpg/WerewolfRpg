package net.aesten.werewolfmc.backend;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.controllers.*;
import net.aesten.werewolfmc.backend.models.ItemStats;
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
import java.io.File;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.util.*;

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
    private final List<UUID> tokens = new ArrayList<>();

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
                .addAnnotatedClass(ItemStats.class)
                .buildMetadata();

        SessionFactory sessionFactory = metadata.buildSessionFactory();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Timestamp.class, new UnixTimestampTypeAdapter())
                .create();
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
        LeaderboardController lbc = new LeaderboardController(sessionFactory);
        MatchHistoryController mhc = new MatchHistoryController(sessionFactory);
        GameDataController gdc = new GameDataController();

        if (config.getBackendCorsEnabled().get() && !config.getBackendCorsAllowedHosts().get().isEmpty()) {
            app = Javalin.create(jConfig -> {
                jConfig.plugins.enableCors(cors -> cors.add(it -> config.getBackendCorsAllowedHosts().get().forEach(it::allowHost)));
                jConfig.jsonMapper(gsonMapper);
            });
        } else {
            app = Javalin.create(jConfig -> {
                jConfig.plugins.enableCors(cors -> cors.add(CorsPluginConfig::anyHost));
                jConfig.jsonMapper(gsonMapper);
            });
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

        app.get("/maps/thumbnails/*", ctx -> {
            String filePath = ctx.path().substring("/maps/thumbnails/".length());
            File file = new File(WerewolfPlugin.getPlugin().getDataFolder(), "werewolf-maps/thumbnails/" + filePath);
            if (file.exists()) {
                byte[] fileContent = Files.readAllBytes(file.toPath());
                ctx.contentType("image/png");
                ctx.result(fileContent);
            } else {
                File file2 = new File(WerewolfPlugin.getPlugin().getDataFolder(), "werewolf-maps/thumbnails/" + "image.png");
                if (file2.exists()) {
                    byte[] fileContent = Files.readAllBytes(file2.toPath());
                    ctx.contentType("image/png");
                    ctx.result(fileContent);
                } else {
                    ctx.status(404);
                }
            }
        });
        app.get("/api/maps", gdc::apiGetMaps);
        app.get("/api/roles", gdc::apiGetRolesFactions);

        app.post("/api/admin/player", pdc::apiRegisterPlayer);
        app.put("/api/admin/player", pdc::apiUpdatePlayer);
        app.delete("/api/admin/player/{minecraft_id}", pdc::apiDeletePlayerByDiscordId);
        app.get("/api/player/{minecraft_id}", pdc::apiGetPlayerFromMinecraftId);
        app.get("/api/player/discord/{discord_id}", pdc::apiGetPlayerFromDiscordId);
        app.get("/api/players", pdc::apiGetAllPlayerData);

        app.post("/api/admin/match", mrc::apiRecordMatch);
        app.put("/api/admin/match/{match_id}", mrc::apiUpdateMatchRecord);
        app.delete("/api/admin/match/{match_id}", mrc::apiDeleteMatch);
        app.get("/api/match/{match_id}", mrc::apiGetMatchRecord);

        app.post("/api/admin/stats", psc::apiSavePlayerStats);
        app.put("/api/admin/stats/{id}", psc::apiUpdateStats);
        app.delete("/api/admin/stats/{match_id}", psc::apiDeleteStatsByMatchId);
        app.delete("/api/admin/stats/{minecraft_id}", psc::apiDeleteStatsByPlayerId);
        app.delete("/api/admin/stats/{id}", psc::apiDeleteStatsByPlayerIdAndMatchId);
        app.get("/api/stats/match/{match_id}", psc::apiGetAllPlayerStatsOfMatch);
        app.get("/api/stats/{minecraft_id}", psc::apiGetGlobalStatsOfPlayer);

        app.get("/api/leaderboard", lbc::apiGetPlayerIds); //has query parameters for pagination

        app.get("/api/matches", mhc::apiGetMatchHistory); //has query parameters for pagination
        app.get("/api/match/player/{minecraft_id}", mhc::apiGetMatchHistoryOfPlayer); //has query parameters for pagination

        new Thread(() -> app.start(config.getBackendPort().get())).start();

        generateToken(0);
    }

    public Javalin getApp() {
        return app;
    }

    public static void init() {
        WerewolfPlugin.logConsole("Enabling Javalin backend");
        AzaleaConfigurationApi.load(WerewolfPlugin.getPlugin(), config);
        File file = new File(WerewolfPlugin.getPlugin().getDataFolder() + File.separator + "werewolf-maps" + File.separator + "thumbnails");
        if (!file.exists() && !file.mkdir()) throw new RuntimeException("Error creating thumbnails directory");
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
            return false;
        }
        return this.tokens.contains(tokenId);
    }

    public void generateToken(Integer timeoutHours) {
        UUID token = UUID.randomUUID();
        WerewolfPlugin.logConsole("Generated token: " + token + " (valid " + (timeoutHours <= 0 ? "unlimited" : timeoutHours) + " hours)");
        if (timeoutHours > 0) WerewolfUtil.runDelayedTask(20 * 3600 * timeoutHours, this::expireToken, token);
    }

    private void expireToken(UUID token) {
        tokens.remove(token);
    }

    public static WerewolfBackend getBackend() {
        return backend;
    }

    public static BackendConfig getConfig() {
        return config;
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
