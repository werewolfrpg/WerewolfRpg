package net.aesten.werewolfmc.bot;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.commands.BotCommand;
import net.aesten.werewolfmc.bot.commands.CommandManager;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.List;

public class WerewolfBot {
    private static WerewolfBot bot;
    private static final BotConfig config = new BotConfig();
    private final JDA jda;
    private boolean isConfigured;

    private WerewolfBot() {
        isConfigured = config.getGuildId().get() != 0 && config.getVcId().get() != 0 && config.getLcId().get() != 0;

        JDABuilder builder = JDABuilder.createDefault(config.getToken().get());
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("Werewolf Minigame"));
        builder.enableIntents(List.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS));
        jda = builder.build();
        CommandManager manager = new CommandManager();
        jda.addEventListener(manager);
        jda.addEventListener(new RegistrationListener());
        jda.updateCommands().addCommands(manager.getCommands().stream().map(BotCommand::getCommand).toList()).queue();

        //Wait until JDA starts
        try {
            jda.awaitReady();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public JDA getJda() {
        return jda;
    }

    public static BotConfig getConfig() {
        return config;
    }

    public boolean isConfigured() {
        return isConfigured;
    }

    public void setConfigured(boolean isConfigured) {
        this.isConfigured = isConfigured;
    }

    public Guild getGuild() {
        return jda.getGuildById(config.getGuildId().get());
    }

    public VoiceChannel getVc() {
        return jda.getVoiceChannelById(config.getVcId().get());
    }

    public TextChannel getLc() {
        return jda.getTextChannelById(config.getLcId().get());
    }

    public static void init() {
        WerewolfPlugin.logConsole("Enabling Discord bot");
        AzaleaConfigurationApi.load(WerewolfPlugin.getPlugin(), config);
        if (config.getToken().get().equals("")) {
            bot = null;
            WerewolfPlugin.logConsole("Discord bot token isn't defined, not enabling");
        } else if (WerewolfBackend.getBackend() == null) {
            bot = null;
            WerewolfPlugin.logConsole("Javalin backend is not enabled, cannot enable Discord bot");
        } else {
            bot = new WerewolfBot();
            WerewolfPlugin.logConsole("Discord bot has been enabled");
        }
    }

    public static void shutDown() {
        if (bot != null) {
            WerewolfPlugin.logConsole("Shutting down Discord bot");
            bot.getJda().shutdown();
            AzaleaConfigurationApi.save(WerewolfPlugin.getPlugin(), config);
        }
    }

    public static WerewolfBot getBot() {
        return bot;
    }
}
