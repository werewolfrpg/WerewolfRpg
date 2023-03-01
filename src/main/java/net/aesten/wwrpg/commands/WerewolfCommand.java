package net.aesten.wwrpg.commands;

import net.aesten.wwrpg.commands.admin.*;
import net.azalealibrary.command.CommandNode;

public class WerewolfCommand extends CommandNode {
    public WerewolfCommand() {
        super("/ww",
                new GameCommand(),
                new SettingCommand(),
                new MapCommand(),
                new TpCommand(),
                new UtilCommand());
    }


    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww";
    }





}
