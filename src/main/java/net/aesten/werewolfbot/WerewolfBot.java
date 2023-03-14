package net.aesten.werewolfbot;

import net.aesten.config.GlobalConfig;
import net.aesten.werewolfbot.commands.GlobalCommands;
import net.aesten.werewolfbot.events.CommandEvent;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.ArrayList;
import java.util.List;

public class WerewolfBot {
    private final List<Guild> subscribedGuilds;
    private
    private final JDA jda;

    public WerewolfBot(String botToken) {
        JDABuilder builder = JDABuilder.createDefault(botToken);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("WerewolfRPG"));
        builder.enableIntents(List.of(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS));
        jda = builder.build();
        jda.addEventListener(new CommandEvent());
        jda.updateCommands().addCommands(GlobalCommands.subscribeCommand).queue();

        //todo read db to fill guild list
        subscribedGuilds = new ArrayList<>(QueryManager.requestGuildIdList().stream().map(jda::getGuildById).toList());
    }

    public JDA getJda() {
        return jda;
    }

    public List<Guild> getSubscribedGuilds() {
        return subscribedGuilds;
    }

}
