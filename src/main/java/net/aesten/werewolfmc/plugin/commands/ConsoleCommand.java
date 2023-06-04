package net.aesten.werewolfmc.plugin.commands;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ConsoleCommand extends CommandNode {
    public ConsoleCommand() {
        super("token");
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        if (!sender.equals(Bukkit.getConsoleSender())) {
            WerewolfUtil.sendErrorText(sender, "Console-only command");
        }
        if (arguments.size() != 1) {
            WerewolfUtil.sendErrorText(sender, "Expects a single positive integer argument");
        }
        int timeout = arguments.find(0, "timeout", Integer::parseInt);
        WerewolfBackend.getBackend().generateToken(timeout);
    }
}
