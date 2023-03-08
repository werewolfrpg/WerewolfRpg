package net.aesten.wwrpg.commands.subcommands;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.map.WerewolfMap;
import net.aesten.wwrpg.shop.Facing;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class MapCommand extends CommandNode {
    public MapCommand() {
        super("map",
                new Select(),
                new List(),
                new Create(),
                new Delete(),
                new Shop(),
                new Skeleton()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww map -> help");
            WerewolfUtil.sendHelpText(player, "/ww map select <map> -> select the map to work on");
            WerewolfUtil.sendHelpText(player, "/ww map list -> list all available maps");
            WerewolfUtil.sendHelpText(player, "/ww map create <name> <world> -> create a new map");
            WerewolfUtil.sendHelpText(player, "/ww map delete <map> -> delete map");
            WerewolfUtil.sendHelpText(player, "/ww map shop [...] -> shop spawning commands");
            WerewolfUtil.sendHelpText(player, "/ww map skeleton [...] -> skeleton visualization commands");
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
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfMap map = WerewolfGame.getMapManager().getMapFromName(arguments.get(0));
            if (map == null) {
                WerewolfUtil.sendErrorText(sender, "There is no such map");
            } else {
                if (sender instanceof Player player) {
                    WerewolfGame.getMapManager().getHelper().selectMap(player, map);
                    WerewolfUtil.sendPluginText(sender, "Selected map " + ChatColor.LIGHT_PURPLE + map.getName());
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.select";
        }
    }

    private static final class List extends CommandNode {
        public List() {
            super("list");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfUtil.sendPluginText(sender, "Loaded maps:");
            WerewolfGame.getMapManager().getMaps().keySet().forEach(s -> WerewolfUtil.sendPluginText(sender, s));
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.select";
        }
    }

    private static final class Create extends CommandNode {
        public Create() {
            super("create");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                return java.util.List.of("<map-name>");
            } else if (arguments.size() == 2) {
                return WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().stream().toList();
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 2) {
                org.bukkit.World world = arguments.find(1, "world", WerewolfGame.getMapManager().getWorldManager()::getWorldFromName);
                if (WerewolfGame.getMapManager().getMapFromName(arguments.get(0)) == null) {
                    if (WerewolfGame.getMapManager().createMap(arguments.get(0), world)) {
                        WerewolfUtil.sendPluginText(sender, "New map created: " + arguments.get(0));
                    } else {
                        WerewolfUtil.sendErrorText(sender, "Map creation failed");
                    }
                } else {
                    WerewolfUtil.sendErrorText(sender, "Map name already taken");
                }
            } else {
                WerewolfUtil.sendErrorText(sender, "Arguments length is incorrect");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.create";
        }
    }

    private static final class Delete extends CommandNode {
        public Delete() {
            super("delete");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                WerewolfMap map = arguments.find(0, "map", WerewolfGame.getMapManager()::getMapFromName);
                if (WerewolfGame.getMapManager().deleteMap(map)) {
                    WerewolfUtil.sendPluginText(sender, "Map deleted :" + arguments.get(0));
                } else {
                    WerewolfUtil.sendErrorText(sender, "Map deletion failed");
                }
            } else {
                WerewolfUtil.sendErrorText(sender, "Arguments length is incorrect");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.delete";
        }
    }

    private static final class Shop extends CommandNode {
        public Shop() {
            super("shop", new Summon());
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendHelpText(player, "/ww map shop -> help");
                WerewolfUtil.sendHelpText(player, "/ww map shop summon -> summon a shop villager");
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
                    WerewolfUtil.sendHelpText(player, "/ww map shop summon -> help");
                    WerewolfUtil.sendHelpText(player, "/ww map shop summon basic <x> <y> <z> <facing> -> summon a basic shop villager");
                    WerewolfUtil.sendHelpText(player, "/ww map shop summon special <x> <y> <z> <facing> -> summon a special shop villager");
                }
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) {
                    return java.util.List.of("basic, special");
                } else if (arguments.size() == 2) {
                    return java.util.List.of("<x>");
                } else if (arguments.size() == 3) {
                    return java.util.List.of("<y>");
                } else if (arguments.size() == 4) {
                    return java.util.List.of("<z>");
                } else if (arguments.size() == 5) {
                    return Arrays.stream(Facing.values()).map(Enum::name).toList();
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (arguments.size() != 5) {
                    help(sender);
                } else {
                    if (sender instanceof Player player) {
                        org.bukkit.World world = WerewolfGame.getMapManager().getHelper().getSelectedMap(player).getWorld();
                        Double x = arguments.find(1, "x", Double::parseDouble);
                        Double y = arguments.find(2, "y", Double::parseDouble);
                        Double z = arguments.find(3, "z", Double::parseDouble);
                        Float yaw = arguments.find(4, "facing", s -> Facing.valueOf(s).getYaw());
                        Location location = new Location(world, x, y, z, yaw, 0);
                        if (arguments.get(0).equals("basic")) {
                            WerewolfGame.getShopManager().summonBasicShopVillager(location);
                            WerewolfUtil.sendPluginText(sender, "Basic villager summoned at: (" + x + "," + y + "," + z + ")");
                        } else if (arguments.get(0).equals("special")) {
                            WerewolfGame.getShopManager().summonSpecialShopVillager(location);
                            WerewolfUtil.sendPluginText(sender, "Special villager summoned at: (" + x + "," + y + "," + z + ")");
                        } else {
                            WerewolfUtil.sendErrorText(sender, "Not a valid villager type");
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

        public static final java.util.List<ArmorStand> armorStands = new ArrayList<>();

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendHelpText(player, "/ww map skeleton -> help");
                WerewolfUtil.sendHelpText(player, "/ww map skeleton show -> summon armor stands to see spawn points");
                WerewolfUtil.sendHelpText(player, "/ww map skeleton hide -> remove armor stands");
                WerewolfUtil.sendHelpText(player, "/ww map skeleton summon -> summon skeletons at every spawn point");
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
                if (sender instanceof Player player) {
                    org.bukkit.World world = WerewolfGame.getMapManager().getHelper().getSelectedMap(player).getWorld();
                    WerewolfGame.getInstance().getMap().getSkeletonSpawnLocations().forEach(v ->  {
                        ArmorStand armorStand = (ArmorStand) world.spawnEntity(v.toLocation(world), EntityType.ARMOR_STAND);
                        armorStand.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999999, 1));
                        armorStands.add(armorStand);
                    });
                }
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
                armorStands.forEach(Entity::remove);
                armorStands.clear();
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
                if (sender instanceof Player player) {
                    WerewolfGame.getSkeletonManager().summonAllSkeletons(WerewolfGame.getMapManager().getHelper().getSelectedMap(player));
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.skeleton.summon";
            }
        }
    }
}
