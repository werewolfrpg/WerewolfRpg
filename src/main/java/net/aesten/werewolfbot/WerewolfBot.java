package net.aesten.werewolfbot;

import net.aesten.werewolfbot.commands.BotCommand;
import net.aesten.werewolfbot.commands.CommandManager;
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
    private final BotConfig config;
    private final JDA jda;
    private boolean isConfigured;

    public WerewolfBot(BotConfig config) {
        this.config = config;
        this.isConfigured = config.getGuildId().get() != 0 && config.getVcId().get() != 0 && config.getLcId().get() != 0;

        JDABuilder builder = JDABuilder.createDefault(config.getToken().get());
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("WerewolfRPG"));
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

    public BotConfig getConfig() {
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
}
