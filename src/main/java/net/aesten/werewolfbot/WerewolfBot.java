package net.aesten.werewolfbot;

import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfbot.commands.CommandManager;
import net.aesten.werewolfdb.QueryManager;
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

public class WerewolfBot {
    private final List<Guild> subscribedGuilds;
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
        subscribedGuilds = new ArrayList<>(QueryManager.requestGuildIdList().stream().map(jda::getGuildById).toList());
    }

    public JDA getJda() {
        return jda;
    }

    public List<Guild> getSubscribedGuilds() {
        return subscribedGuilds;
    }

    public boolean isSubscribed(Guild guild) {
        return subscribedGuilds.contains(guild);
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
