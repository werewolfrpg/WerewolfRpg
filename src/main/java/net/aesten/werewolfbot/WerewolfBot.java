package net.aesten.werewolfbot;

import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfbot.commands.CommandManager;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WerewolfBot {
    private final List<String> subscribedGuilds;
    private final JDA jda;
    private Session currentSession;

    public WerewolfBot(String botToken) {
        JDABuilder builder = JDABuilder.createDefault(botToken);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("WerewolfRPG"));
        builder.enableIntents(List.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS));
        jda = builder.build();
        CommandManager manager = new CommandManager();
        jda.addEventListener(manager);
        jda.updateCommands().addCommands(manager.getCommands().stream().map(DiscordCommand::getCommand).toList()).queue();
        try {
            jda.awaitReady();
            subscribedGuilds = new ArrayList<>(QueryManager.requestGuildIdList());
            WerewolfRpg.logConsole("Registered guilds: " + subscribedGuilds.stream().map(jda::getGuildById).filter(Objects::nonNull).map(Guild::getName).toList());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public JDA getJda() {
        return jda;
    }

    public List<String> getSubscribedGuilds() {
        return subscribedGuilds;
    }

    public boolean isSubscribed(Guild guild) {
        return subscribedGuilds.contains(guild.getId());
    }

    public void newSession(VoiceChannel vc, TextChannel lc) {
        currentSession = new Session(vc, lc);
    }

    public void endSession() {
        currentSession.terminate();
        currentSession = null;
    }

    public Session getCurrentSession() {
        return currentSession;
    }
}
