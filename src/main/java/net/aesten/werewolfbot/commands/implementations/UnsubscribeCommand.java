package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UnsubscribeCommand extends DiscordCommand {
    public UnsubscribeCommand() {
        super("unsubscribe", "Unregister the server for the bot to disable other commands", DefaultMemberPermissions.DISABLED, Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            if (WerewolfRpg.getBot().isSubscribed(event.getGuild())) {
                unsubscribe(Objects.requireNonNull(event.getGuild()));
                event.reply("Unsubscription succeeded").queue();
            } else {
                event.reply("This guild is not registered").queue();
            }
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }

    private void unsubscribe(Guild guild) {
        WerewolfRpg.getBot().getSubscribedGuilds().remove(guild);
        QueryManager.removeGuild(guild.getId());
    }
}
