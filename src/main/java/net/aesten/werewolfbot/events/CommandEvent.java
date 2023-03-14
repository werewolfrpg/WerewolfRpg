package net.aesten.werewolfbot.events;

import net.aesten.werewolfbot.commands.GlobalCommands;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

public class CommandEvent extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandInteraction interaction = event.getInteraction();
        if (interaction.isGlobalCommand()) {
            if (event.getName().equals("subscribe")) {
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
    }
}
