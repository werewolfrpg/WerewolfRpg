package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class UnbindCommand extends DiscordCommand {
    public UnbindCommand() {
        super("unbind", "Removes the link between your discord account and your Minecraft ID", DefaultMemberPermissions.ENABLED, Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            if (!WerewolfRpg.getBot().isSubscribed(Objects.requireNonNull(event.getGuild()))) {
                event.reply("This server is not subscribed, ask an admin to subscribe the server").setEphemeral(true).queue();
                return;
            }

            String mcid = QueryManager.getMcIdOfDiscordUser(event.getUser().getId());
            if (mcid.equals("")) {
                event.reply("No ID is linked to your discord account").setEphemeral(true).queue();
            } else {
                QueryManager.removeBinding(event.getUser().getId());
                event.reply("Account unbinding succeeded").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }
}
