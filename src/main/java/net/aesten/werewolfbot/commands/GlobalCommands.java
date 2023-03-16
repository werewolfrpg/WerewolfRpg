package net.aesten.werewolfbot.commands;


import  net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class GlobalCommands {
    public static final CommandData subscribeCommand = Commands
            .slash("subscribe", "registers the server for the bot to enable guild commands")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED);

    public static final CommandData unsubscribeCommand = Commands
            .slash("unsubscribe", "unregisters the server and deletes all related features and data")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED);

    public static void subscribe(Guild guild) {
        WerewolfRpg.getBot().getSubscribedGuilds().add(guild);
        QueryManager.addGuild(guild.getId());
        //registerGuildCommands(guild)
    }

    public static void unsubscribe(Guild guild) {
        WerewolfRpg.getBot().getSubscribedGuilds().remove(guild);
        QueryManager.removeGuild(guild.getId());
        //unregisterGuildCommands
        //delete all bot messages in guild
    }
}
