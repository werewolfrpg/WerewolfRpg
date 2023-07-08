package net.aesten.werewolfmc.plugin.commands;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.statistics.ScoreManager;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ConsoleCommand extends CommandNode {
    public ConsoleCommand() {
        super("token",
                new Token(),
                new Wipe());
    }

    private static final class Token extends CommandNode {
        public Token() {
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

    private static final class Wipe extends CommandNode {
        public Wipe() {
            super("wipe");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (!sender.equals(Bukkit.getConsoleSender())) {
                WerewolfUtil.sendErrorText(sender, "Console-only command");
            }
            WerewolfBackend.getBackend().getDbc().wipeDatabase().join();
            for (Player player : Bukkit.getOnlinePlayers()) {
                ScoreManager scoreManager = WerewolfGame.getScoreManager();
                scoreManager.assignRole(player, WerewolfBot.getBot().getGuild());
                scoreManager.assignPrefixSuffix(player);
            }
            WerewolfUtil.sendPluginText(sender, "Database has been wiped");
        }
    }
}
