package net.aesten.werewolfbot.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.util.List;

public interface BotCommand {
    String getName();
    CommandData getCommand();
    void execute(SlashCommandInteractionEvent event);
    List<String> complete(CommandAutoCompleteInteractionEvent event);
}
