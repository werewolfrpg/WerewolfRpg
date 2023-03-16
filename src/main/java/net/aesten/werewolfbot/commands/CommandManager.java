package net.aesten.werewolfbot.commands;

import net.aesten.werewolfbot.commands.implementations.SubscribeCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandManager extends ListenerAdapter {
    private final List<AbstractCommand> commands;

    public CommandManager() {
        commands = new ArrayList<>();
        commands.add(new SubscribeCommand());
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandInteraction interaction = event.getInteraction();
        Optional<AbstractCommand> abstractCommand = commands.stream().filter(cmd -> cmd.getName().equals(event.getName())).findAny();
        abstractCommand.ifPresent(cmd -> cmd.execute(event));

    }


}
