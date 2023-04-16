package net.aesten.werewolfrpg.plugin.commands.subcommands;

import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.aesten.werewolfrpg.plugin.data.Role;
import net.aesten.werewolfrpg.plugin.data.RolePool;
import net.aesten.werewolfrpg.plugin.map.WerewolfMap;
import net.aesten.werewolfrpg.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameCommand extends CommandNode {
    public GameCommand() {
        super("game",
                new Start(),
                new Stop(),
                new Gather(),
                new Map(),
                new Players(),
                new Roles()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww game -> help");
            WerewolfUtil.sendHelpText(player, "/ww game start -> start game");
            WerewolfUtil.sendHelpText(player, "/ww game stop -> interrupt game");
            WerewolfUtil.sendHelpText(player, "/ww game gather -> teleport all participants to selected map");
            WerewolfUtil.sendHelpText(player, "/ww game map <map> -> change map");
            WerewolfUtil.sendHelpText(player, "/ww game roles [...] -> change the number of each role");
            WerewolfUtil.sendHelpText(player, "/ww game players [...] -> manage players");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
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
                } else {
                    WerewolfUtil.sendErrorText(sender, WerewolfGame.getStatusMessage());
                }
            } else {
                WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
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
                WerewolfUtil.sendErrorText(sender, "There is currently no ongoing game");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.stop";
        }
    }

    private static final class Gather extends CommandNode {
        public Gather() {
            super("gather");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getInstance().isPlaying()) {
                WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
            } else if (WerewolfGame.getInstance().getMap() == null) {
                WerewolfUtil.sendErrorText(sender, "No map has been set for the minigame");
            } else {
                WerewolfGame.getInstance().getParticipants().forEach(player -> {
                    player.teleport(WerewolfGame.getInstance().getMap().getMapSpawn());
                    player.setGameMode(GameMode.ADVENTURE);
                });
                WerewolfUtil.sendPluginText(sender, "All players have been gathered to the selected map");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.gather";
        }
    }

    private static final class Map extends CommandNode {
        public Map() {
            super("map");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getInstance().isPlaying()) {
                WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
            } else {
                WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
                if (map == null) {
                    WerewolfUtil.sendErrorText(sender, "There is no such map");
                } else {
                    WerewolfGame.getInstance().setMap(map);
                    WerewolfUtil.sendPluginText(sender, "Changed map to " + ChatColor.LIGHT_PURPLE + map.getName());
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
                WerewolfUtil.sendHelpText(player, "/ww game players -> help");
                WerewolfUtil.sendHelpText(player, "/ww game players count -> number of players");
                WerewolfUtil.sendHelpText(player, "/ww game players list -> list players");
                WerewolfUtil.sendHelpText(player, "/ww game players add <player> -> add a player");
                WerewolfUtil.sendHelpText(player, "/ww game players add-all -> add all online players");
                WerewolfUtil.sendHelpText(player, "/ww game players remove <player> -> remove a player");
                WerewolfUtil.sendHelpText(player, "/ww game players remove-all -> remove all online players");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
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
                WerewolfUtil.sendPluginText(sender, "There are " + ChatColor.LIGHT_PURPLE + count + ChatColor.RESET + " players");
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
                WerewolfUtil.sendPluginText(sender, "Participants: " + players);
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
                    return Collections.emptyList();
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
                                WerewolfUtil.sendPluginText(sender, "Added " + ChatColor.LIGHT_PURPLE + player.getName());
                            } else {
                                WerewolfUtil.sendErrorText(sender, "Player is already a participant");
                            }
                        } else {
                            WerewolfUtil.sendErrorText(sender, "Player not found");
                        }
                    } else {
                        WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
                    }
                } else {
                    WerewolfUtil.sendErrorText(sender, "Arguments length is incorrect");
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
                        WerewolfUtil.sendPluginText(sender, "Added " + ChatColor.LIGHT_PURPLE + player.getName());
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
                    return Collections.emptyList();
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
                                WerewolfUtil.sendPluginText(sender, "Removed " + ChatColor.LIGHT_PURPLE + player.getName());
                            } else {
                                WerewolfUtil.sendErrorText(sender, "Player is not a participant");
                            }
                        } else {
                            WerewolfUtil.sendErrorText(sender, "Player not found");
                        }
                    } else {
                        WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
                    }
                } else {
                    WerewolfUtil.sendErrorText(sender, "Arguments length is incorrect");
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
                        WerewolfUtil.sendPluginText(sender, "Removed " + ChatColor.LIGHT_PURPLE + player.getName());
                    }
                }
            }
        }
    }

    private static final class Roles extends CommandNode {
        public Roles() {
            super("roles",
                    new List(),
                    new Reset(),
                    new Werewolf(),
                    new Traitor(),
                    new Vampire(),
                    new Possessed()
            );
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendHelpText(player, "/ww game roles -> help");
                WerewolfUtil.sendHelpText(player, "/ww game roles list -> list the current configuration of roles");
                WerewolfUtil.sendHelpText(player, "/ww game roles reset -> reset to only 1 werewolf");
                WerewolfUtil.sendHelpText(player, "/ww game roles werewolf <int> -> change the number of werewolves");
                WerewolfUtil.sendHelpText(player, "/ww game roles traitor <int> -> change the number of traitors");
                WerewolfUtil.sendHelpText(player, "/ww game roles vampire <int> -> change the number of vampires");
                WerewolfUtil.sendHelpText(player, "/ww game roles possessed <int> -> change the number of possessed");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.game.roles";
        }

        private static final class List extends CommandNode {
            public List() {
                super("list");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                RolePool pool = WerewolfGame.getInstance().getPool();
                WerewolfUtil.sendPluginText(sender, "Current role configuration:");
                WerewolfUtil.sendPluginText(sender, "Werewolves: " + pool.getWerewolfNumber(), Role.WEREWOLF.color);
                WerewolfUtil.sendPluginText(sender, "Traitors: " + pool.getTraitorNumber(), Role.TRAITOR.color);
                WerewolfUtil.sendPluginText(sender, "Vampires: " + pool.getVampireNumber(), Role.VAMPIRE.color);
                WerewolfUtil.sendPluginText(sender, "Possessed: " + pool.getPossessedNumber(), Role.POSSESSED.color);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.list";
            }
        }

        private static final class Reset extends CommandNode {
            public Reset() {
                super("reset");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                RolePool pool = WerewolfGame.getInstance().getPool();
                pool.setWerewolfNumber(1);
                pool.setTraitorNumber(0);
                pool.setVampireNumber(0);
                pool.setPossessedNumber(0);
                WerewolfUtil.sendPluginText(sender, "Roles are reset");
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.reset";
            }
        }

        private static final class Werewolf extends CommandNode {
            public Werewolf() {
                super("werewolf");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer werewolfNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setWerewolfNumber(werewolfNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.WEREWOLF.color + Role.WEREWOLF.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + werewolfNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.werewolf";
            }
        }

        private static final class Traitor extends CommandNode {
            public Traitor() {
                super("traitor");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer traitorNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setTraitorNumber(traitorNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.TRAITOR.color + Role.TRAITOR.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + traitorNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.traitor";
            }
        }

        private static final class Vampire extends CommandNode {
            public Vampire() {
                super("vampire");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer vampireNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setVampireNumber(vampireNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.VAMPIRE.color + Role.VAMPIRE.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + vampireNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.vampire";
            }
        }

        private static final class Possessed extends CommandNode {
            public Possessed() {
                super("possessed");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer possessedNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setPossessedNumber(possessedNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.POSSESSED.color + Role.POSSESSED.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + possessedNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.game.roles.possessed";
            }
        }
    }
}
