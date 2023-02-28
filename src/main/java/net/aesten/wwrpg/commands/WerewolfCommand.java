package net.aesten.wwrpg.commands;

import net.azalealibrary.command.CommandNode;

public class WerewolfCommand extends CommandNode {
    public WerewolfCommand() {
        super("/ww" /*, children...*/);
    }


    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww";
    }





}
