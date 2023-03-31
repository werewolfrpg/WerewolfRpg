package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfdb.QueryManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

import java.util.Collections;
import java.util.List;

public class UnbindCommand extends DiscordCommand {
    public UnbindCommand() {
        super("unbind", "Removes the link between your discord account and your Minecraft ID", DefaultMemberPermissions.ENABLED, Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
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
