package net.aesten.werewolfmc.plugin.commands.subcommands;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ScoreCommand extends CommandNode {
    public ScoreCommand() {
        super("score", new Add(), new Set());
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww score -> help");
            WerewolfUtil.sendHelpText(player, "/ww score add <score> <player> -> adds the specified score to the selected player");
            WerewolfUtil.sendHelpText(player, "/ww score set <score> <player> -> sets the specified score for the selected player");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    private static final class Add extends CommandNode {
        public Add() {
            super("add");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                return List.of("<int>");
            } else if (arguments.size() == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            int gain = arguments.find(0, "gain", Integer::parseInt);
            Player player = arguments.find(1, "player", Bukkit::getPlayer);
            if (gain < 0) {
                WerewolfUtil.sendErrorText(sender, "Score shouldn't be negative");
                return;
            }
            WerewolfBackend backend = WerewolfBackend.getBackend();
            if (backend == null) {
                WerewolfUtil.sendErrorText(sender, "Backend is not running");
                return;
            }
            UUID uuid = player.getUniqueId();
            long dcId = backend.getPdc().getDiscordIdOfPlayer(uuid).join();
            int score = backend.getPdc().addScoreToPlayer(uuid, gain).join().getScore();
            WerewolfGame.getScoreManager().assignRole(dcId, WerewolfBot.getBot().getGuild(), WerewolfGame.getScoreManager().getScoreRank(score));
            WerewolfGame.getScoreManager().assignPrefixSuffix(player, score);
            WerewolfUtil.sendPluginText(sender, arguments.get(1) + " now has " + score + " score");
        }

        @Override
        public String getPermission() {
            return "werewolf.cmd.ww.score.add";
        }
    }

    private static final class Set extends CommandNode {
        public Set() {
            super("set");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                return List.of("<int>");
            } else if (arguments.size() == 2) {
                return Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            int score = arguments.find(0, "score", Integer::parseInt);
            Player player = arguments.find(1, "player", Bukkit::getPlayer);
            if (score < 0) {
                WerewolfUtil.sendErrorText(sender, "Score shouldn't be negative");
                return;
            }
            WerewolfBackend backend = WerewolfBackend.getBackend();
            if (backend == null) {
                WerewolfUtil.sendErrorText(sender, "Backend is not running");
                return;
            }
            UUID uuid = player.getUniqueId();
            long dcId = backend.getPdc().getDiscordIdOfPlayer(uuid).join();
            backend.getPdc().setScoreOfPlayer(uuid, score).join();
            WerewolfGame.getScoreManager().assignRole(dcId, WerewolfBot.getBot().getGuild(), WerewolfGame.getScoreManager().getScoreRank(score));
            WerewolfGame.getScoreManager().assignPrefixSuffix(player, score);
            WerewolfUtil.sendPluginText(sender, arguments.get(0) + " now has " + score + " score");
        }

        @Override
        public String getPermission() {
            return "werewolf.cmd.ww.score.set";
        }
    }
}
