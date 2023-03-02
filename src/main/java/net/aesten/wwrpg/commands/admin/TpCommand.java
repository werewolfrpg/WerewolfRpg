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

import java.util.Collections;
import java.util.List;

public class TpCommand extends CommandNode {
    public TpCommand() {
        super("tp");
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww tp -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww tp <map> -> teleport to map");
            WerewolfUtil.sendCommandHelp(player, "/ww tp <map> @a -> teleport all online players to map");
            WerewolfUtil.sendCommandHelp(player, "/ww tp <map> players -> teleport all participants to map");
        }
    }

    @Override
    public List<String> complete(CommandSender sender, Arguments arguments) {
        if (arguments.size() == 1) {
            return WerewolfGame.getMapManager().getMaps().keySet().stream().toList();
        } else if (arguments.size() == 2) {
            return List.of("@a", "players");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        if (!WerewolfGame.getInstance().isPlaying()) {
            if (arguments.size() == 0) {
                help(sender);
            } else if (arguments.size() == 1) {
                WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
                if (map != null) {
                    if (sender instanceof Player player) {
                        player.teleport(map.getMapSpawn());
                        WerewolfUtil.sendPluginText(player, "You teleported to " + ChatColor.LIGHT_PURPLE + map.getMapName());
                    }
                } else {
                    WerewolfUtil.sendCommandError(sender, "There is no such map");
                }
            } else if (arguments.size() == 2) {
                WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
                if (map != null) {
                    if (arguments.get(1).equals("@a")) {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.teleport(map.getMapSpawn());
                            WerewolfUtil.sendPluginText(player, "You teleported to " + ChatColor.LIGHT_PURPLE + map.getMapName());
                        }
                    } else if (arguments.get(1).equals("players")) {
                        for (Player player : WerewolfGame.getInstance().getParticipants()) {
                            player.teleport(map.getMapSpawn());
                            WerewolfUtil.sendPluginText(player, "You teleported to " + ChatColor.LIGHT_PURPLE + map.getMapName());
                        }
                    } else {
                        WerewolfUtil.sendCommandError(sender, "'" + arguments.get(1) + "' is an invalid argument");
                    }
                } else {
                    WerewolfUtil.sendCommandError(sender, "There is no such map");
                }
            } else {
                WerewolfUtil.sendCommandError(sender, "Arguments length is incorrect");
            }
        } else {
            WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
        }
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.tp";
    }
}
