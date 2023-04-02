package net.aesten.werewolfbot.commands;

import net.aesten.werewolfbot.commands.implementations.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandManager extends ListenerAdapter {
    private final List<DiscordCommand> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new SubscribeCommand());
        commands.add(new UnsubscribeCommand());
        commands.add(new SessionCommand());
        commands.add(new RegisterButtonCommand());
        commands.add(new UnbindCommand());
    }

    public List<DiscordCommand> getCommands() {
        return commands;
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Optional<DiscordCommand> abstractCommand = commands.stream().filter(cmd -> cmd.getName().equals(event.getName())).findAny();
        abstractCommand.ifPresent(cmd -> cmd.execute(event));
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        Optional<DiscordCommand> abstractCommand = commands.stream().filter(cmd -> cmd.getName().equals(event.getName())).findAny();
        abstractCommand.ifPresent(cmd ->
                event.replyChoices(
                        sortPartialMatches(event.getFocusedOption().getValue(), cmd.complete(event))
                        .stream()
                        .map(s -> new Command.Choice(s, s))
                        .toList()
                ).queue());
    }

    private static List<String> sortPartialMatches(String s, Iterable<String> completeList) {
        List<String> matchingList = new ArrayList<>();
        StringUtil.copyPartialMatches(s, completeList, matchingList);
        Collections.sort(matchingList);
        return matchingList;
    }
}
