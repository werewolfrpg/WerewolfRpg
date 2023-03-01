package net.aesten.wwrpg.commands.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.map.WerewolfMap;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MapCommand extends CommandNode {
    public MapCommand() {
        super("map",
                new Select(),
                new Edit()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww map -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww map select <map> -> select the map to work on");
            WerewolfUtil.sendCommandHelp(player, "/ww map edit [...] -> map editing commands");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.map";
    }

    private static final class Select extends CommandNode {
        public Select() {
            super("select");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : List.of();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
            if (map == null) {
                WerewolfUtil.sendCommandError(sender, "There is no such map");
            } else {
                WerewolfGame.getInstance().setMap(map);
                WerewolfUtil.sendCommandText(sender, "Selected map " + ChatColor.LIGHT_PURPLE + map.getMapName());
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.select";
        }
    }

    private static final class Edit extends CommandNode {
        public Edit() {
            super("edit");
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww map edit -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit name -> select the map to work on");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit shop -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit skeleton -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit skull -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit world -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit spawn -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit border-center -> map editing commands");
                WerewolfUtil.sendCommandHelp(player, "/ww map edit border-size -> map editing commands");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.edit";
        }


    }
}
