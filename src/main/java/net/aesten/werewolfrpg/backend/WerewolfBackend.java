package net.aesten.werewolfrpg.backend;

import io.javalin.Javalin;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.azalealibrary.configuration.AzaleaConfigurationApi;

public class WerewolfBackend {
    private static WerewolfBackend backend;
    private final static BackendConfig config = new BackendConfig();
    private final Javalin app;

    private WerewolfBackend() {
        app = Javalin.create().start(8080);
        app.get("/", ctx -> ctx.result("Hello, I'm Javalin"));
        WerewolfRpg.logConsole("Javalin backend enabled");
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
            backend = new WerewolfBackend();
            WerewolfRpg.logConsole("Javalin backend has been enabled");
        }
    }

    public static void shutDown() {
        if (backend != null) {
            WerewolfRpg.logConsole("Shutting down Javalin backend");
            backend.getApp().close();
            AzaleaConfigurationApi.save(WerewolfRpg.getPlugin(), config);
        }
    }


    public static WerewolfBackend getBackend() {
        return backend;
    }
}
