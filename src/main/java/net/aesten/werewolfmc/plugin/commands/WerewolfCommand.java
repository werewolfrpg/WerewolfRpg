package net.aesten.werewolfmc.plugin.commands;

import net.aesten.werewolfmc.plugin.commands.subcommands.*;
import net.azalealibrary.command.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WerewolfCommand extends CommandNode {
    public WerewolfCommand() {
        super("ww",
                new GameCommand(),
                new MapCommand(),
                new WorldCommand(),
                new TpCommand(),
                new UtilCommand(),
                new ItemCommand(),
                new SummonCommand(),
                new ScoreCommand()
        );
    }


    @Override
    public String getPermission() {
        return "werewolf.cmd.ww";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return List.of("werewolf");
    }
}
