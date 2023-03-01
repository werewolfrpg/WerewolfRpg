package net.aesten.wwrpg.commands.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.map.WerewolfMap;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GameCommand extends CommandNode {
    public GameCommand() {
        super("game",
                new Start(),
                new Stop(),
                new Map(),
                new Players()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww game -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww game start -> start game");
            WerewolfUtil.sendCommandHelp(player, "/ww game stop -> interrupt game");
            WerewolfUtil.sendCommandHelp(player, "/ww game map <map> -> change map");
            WerewolfUtil.sendCommandHelp(player, "/ww game players [...] -> manage players");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 0) help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.game";
    }

    private static final class Start extends CommandNode {
        public Start() {
            super("start");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (!WerewolfGame.getInstance().isPlaying()) {
                if (WerewolfGame.isReady()) {
                    WerewolfGame.start();
                    WerewolfUtil.sendCommandText(sender, "Starting game");
                } else {
                    WerewolfUtil.sendCommandError(sender, WerewolfGame.getStatusMessage());
                }
            } else {
                WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.start";
        }
    }

    private static final class Stop extends CommandNode {
        public Stop() {
            super("stop");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getInstance().isPlaying()) {
                WerewolfGame.interrupt();
            } else {
                WerewolfUtil.sendCommandError(sender, "There is currently no ongoing game");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.stop";
        }
    }

    private static final class Map extends CommandNode {
        public Map() {
            super("map");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : List.of();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getInstance().isPlaying()) {
                WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
            } else {
                WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
                if (map == null) {
                    WerewolfUtil.sendCommandError(sender, "There is no such map");
                } else {
                    WerewolfGame.getInstance().setMap(map);
                    WerewolfUtil.sendCommandText(sender, "Changed map to " + ChatColor.LIGHT_PURPLE + map.getMapName());
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.map";
        }
    }

    private static final class Players extends CommandNode {
        public Players() {
            super("players",
                    new Count(),
                    new List(),
                    new Add(),
                    new AddAll(),
                    new Remove(),
                    new RemoveAll()
            );
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww game players -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww game players count -> number of players");
                WerewolfUtil.sendCommandHelp(player, "/ww game players list -> list players");
                WerewolfUtil.sendCommandHelp(player, "/ww game players add <player> -> add a player");
                WerewolfUtil.sendCommandHelp(player, "/ww game players add-all -> add all online players");
                WerewolfUtil.sendCommandHelp(player, "/ww game players remove <player> -> remove a player");
                WerewolfUtil.sendCommandHelp(player, "/ww game players remove-all -> remove all online players");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 0) help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.players";
        }

        private static final class Count extends CommandNode {
            public Count() {
                super("count");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                int count = WerewolfGame.getInstance().getParticipants().size();
                WerewolfUtil.sendCommandText(sender, "There are " + ChatColor.LIGHT_PURPLE + count + ChatColor.RESET + " players");
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.players.count";
            }
        }

        private static final class List extends CommandNode {
            public List() {
                super("list");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                String players = ChatColor.LIGHT_PURPLE +
                        String.join(
                                ChatColor.RESET + ", " + ChatColor.LIGHT_PURPLE,
                                WerewolfGame.getInstance().getParticipants().stream().map(Player::getName).toList()
                        );
                WerewolfUtil.sendCommandText(sender, "Participants: " + players);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.players.list";
            }
        }

        private static final class Add extends CommandNode {
            public Add() {
                super("add");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) {
                    java.util.List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                    players.removeAll(WerewolfGame.getInstance().getParticipants());
                    return players.stream().map(Player::getName).toList();
                } else {
                    return java.util.List.of();
                }
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) {
                    if (!WerewolfGame.getInstance().isPlaying()) {
                        Player player = Bukkit.getPlayer(arguments.get(0));
                        if (player != null) {
                            if (!WerewolfGame.getInstance().isParticipant(player)) {
                                WerewolfGame.getInstance().getParticipants().add(player);
                                WerewolfUtil.sendCommandText(sender, "Added " + ChatColor.LIGHT_PURPLE + player.getName());
                            } else {
                                WerewolfUtil.sendCommandError(sender, "Player is already a participant");
                            }
                        } else {
                            WerewolfUtil.sendCommandError(sender, "Player not found");
                        }
                    } else {
                        WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                    }
                } else {
                    WerewolfUtil.sendCommandError(sender, "Arguments length is incorrect");
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.players.add";
            }
        }

        private static final class AddAll extends CommandNode {
            public AddAll() {
                super("add-all");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!WerewolfGame.getInstance().getParticipants().contains(player)) {
                        WerewolfGame.getInstance().getParticipants().add(player);
                        WerewolfUtil.sendCommandText(sender, "Added " + ChatColor.LIGHT_PURPLE + player.getName());
                    }
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.players.add-all";
            }
        }

        private static final class Remove extends CommandNode {
            public Remove() {
                super("remove");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) {
                    return WerewolfGame.getInstance().getParticipants().stream().map(Player::getName).toList();
                } else {
                    return java.util.List.of();
                }
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) {
                    if (!WerewolfGame.getInstance().isPlaying()) {
                        Player player = Bukkit.getPlayer(arguments.get(0));
                        if (player != null) {
                            if (WerewolfGame.getInstance().isParticipant(player)) {
                                WerewolfGame.getInstance().getParticipants().remove(player);
                                WerewolfUtil.sendCommandText(sender, "Removed " + ChatColor.LIGHT_PURPLE + player.getName());
                            } else {
                                WerewolfUtil.sendCommandError(sender, "Player is not a participant");
                            }
                        } else {
                            WerewolfUtil.sendCommandError(sender, "Player not found");
                        }
                    } else {
                        WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                    }
                } else {
                    WerewolfUtil.sendCommandError(sender, "Arguments length is incorrect");
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.players.remove";
            }
        }

        private static final class RemoveAll extends CommandNode {
            public RemoveAll() {
                super("remove-all");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (WerewolfGame.getInstance().getParticipants().contains(player)) {
                        WerewolfGame.getInstance().getParticipants().remove(player);
                        WerewolfUtil.sendCommandText(sender, "Removed " + ChatColor.LIGHT_PURPLE + player.getName());
                    }
                }
            }
        }
    }
}
