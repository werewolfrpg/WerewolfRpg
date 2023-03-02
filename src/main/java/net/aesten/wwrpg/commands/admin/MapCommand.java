package net.aesten.wwrpg.commands.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.map.WerewolfMap;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import net.azalealibrary.configuration.property.PropertyType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class MapCommand extends CommandNode {
    public MapCommand() {
        super("map",
                new Select(),
                new Shop(),
                new Skeleton()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww map -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww map select <map> -> select the map to work on");
            WerewolfUtil.sendCommandHelp(player, "/ww map shop [...] -> shop spawning commands");
            WerewolfUtil.sendCommandHelp(player, "/ww map skeleton [...] -> skeleton visualization commands");
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
                if (sender instanceof Player player) {
                    WerewolfGame.getMapManager().getHelper().selectMap(player, map);
                    WerewolfUtil.sendCommandText(sender, "Selected map " + ChatColor.LIGHT_PURPLE + map.getMapName());
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.select";
        }
    }

    private static final class Shop extends CommandNode {
        public Shop() {
            super("shop", new Summon());
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww map shop -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww map shop summon -> summon a shop villager");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.shop";
        }

        private static final class Summon extends CommandNode {
            public Summon() {
                super("summon");
            }

            private void help(CommandSender sender) {
                if (sender instanceof Player player) {
                    WerewolfUtil.sendCommandHelp(player, "/ww map shop summon -> help");
                    WerewolfUtil.sendCommandHelp(player, "/ww map shop summon basic <x> <y> <z> <facing> -> summon a basic shop villager");
                    WerewolfUtil.sendCommandHelp(player, "/ww map shop summon special <x> <y> <z> <facing> -> summon a special shop villager");
                }
            }

            @Override
            public List<String> complete(CommandSender sender, Arguments arguments) {
                return List.of();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (arguments.size() != 5) {
                    help(sender);
                } else {
                    Double x = arguments.find(1, "x", Double::parseDouble);
                    Double y = arguments.find(2, "y", Double::parseDouble);
                    Double z = arguments.find(3, "z", Double::parseDouble);
                }

            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.shop.summon";
            }
        }
    }

    private static final class Skeleton extends CommandNode {
        public Skeleton() {
            super("skeleton");
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton show -> summon armor stands to see spawn points");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton hide -> remove armor stands");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton summon -> summon a basic skeleton at every spawn points");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.skeleton";
        }


    }
}
