package net.aesten.wwrpg.commands.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.map.WerewolfMap;
import net.aesten.wwrpg.shop.Facing;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
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
                if (arguments.size() == 1) {
                    return List.of("basic, special");
                } else if (arguments.size() == 2) {
                    return List.of("<x>");
                } else if (arguments.size() == 3) {
                    return List.of("<y>");
                } else if (arguments.size() == 4) {
                    return List.of("<z>");
                } else if (arguments.size() == 5) {
                    return Arrays.stream(Facing.values()).map(Enum::name).toList();
                } else {
                    return List.of();
                }
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (arguments.size() != 5) {
                    help(sender);
                } else {
                    if (sender instanceof Player player) {
                        World world = WerewolfGame.getMapManager().getHelper().getSelectedMap(player).getWorld();
                        Double x = arguments.find(1, "x", Double::parseDouble);
                        Double y = arguments.find(2, "y", Double::parseDouble);
                        Double z = arguments.find(3, "z", Double::parseDouble);
                        Float yaw = arguments.find(4, "facing", s -> Facing.valueOf(s).getYaw());
                        Location location = new Location(world, x, y, z, yaw, 0);
                        if (arguments.get(0).equals("basic")) {
                            WerewolfGame.getShopManager().summonBasicShopVillager(location);
                            WerewolfUtil.sendCommandText(sender, "Basic villager summoned at: (" + x + "," + y + "," + z + ")");
                        } else if (arguments.get(0).equals("special")) {
                            WerewolfGame.getShopManager().summonSpecialShopVillager(location);
                            WerewolfUtil.sendCommandText(sender, "Special villager summoned at: (" + x + "," + y + "," + z + ")");
                        } else {
                            WerewolfUtil.sendCommandError(sender, "Not a valid villager type");
                        }
                    }
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
            super("skeleton",
                    new Show(),
                    new Hide(),
                    new Summon()
                    );
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

        private static final class Show extends CommandNode {
            public Show() {
                super("show");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {

            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.skeleton.show";
            }
        }

        private static final class Hide extends CommandNode {
            public Hide() {
                super("hide");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {

            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.skeleton.hide";
            }
        }

        private static final class Summon extends CommandNode {
            public Summon() {
                super("summon");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {

            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.skeleton.summon";
            }
        }
    }
}
