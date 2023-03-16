package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.AbstractCommand;
import net.aesten.werewolfbot.commands.GlobalCommands;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;

public class SubscribeCommand extends AbstractCommand {
    public SubscribeCommand() {
        super("subscribe", "Register the server for the bot to enable guild commands", true, DefaultMemberPermissions.DISABLED);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        if (event.isFromGuild()) {
            if (WerewolfRpg.getBot().getSubscribedGuilds().contains(event.getGuild())) {
                event.getHook().sendMessage("This guild is already registered").queue();
            } else {
                GlobalCommands.subscribe(event.getGuild());
                event.getHook().sendMessage("Subscription succeeded").queue();
            }
        } else {
            event.getHook().sendMessage("You can only register servers!").queue();
        }
    }
}
