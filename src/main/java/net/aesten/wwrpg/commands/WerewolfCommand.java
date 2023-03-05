package net.aesten.wwrpg.commands;

import net.aesten.wwrpg.commands.subcommands.*;
import net.azalealibrary.command.CommandNode;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WerewolfCommand extends CommandNode {
    public WerewolfCommand() {
        super("ww",
                new GameCommand(),
                new SettingCommand(),
                new MapCommand(),
                new WorldCommand(),
                new TpCommand(),
                new UtilCommand());
    }


    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww";
    }

    @NotNull
    @Override
    public List<String> getAliases() {
        return List.of("wwrpg");
    }
}
