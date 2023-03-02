package net.aesten.wwrpg.commands.admin;

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
import java.util.List;

public class MapCommand extends CommandNode {
    public MapCommand() {
        super("map",
                new World(),
                new Select(),
                new Create(),
                new Delete(),
                new Shop(),
                new Skeleton()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww map -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww map world [...] -> manage worlds");
            WerewolfUtil.sendCommandHelp(player, "/ww map select <map> -> select the map to work on");
            WerewolfUtil.sendCommandHelp(player, "/ww map create <name> <world> -> create a new map");
            WerewolfUtil.sendCommandHelp(player, "/ww map delete <map> -> delete map");
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

    private static final class World extends CommandNode {
        public World() {
            super("world",
                    new Create(),
                    new Delete()
            );
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww map world -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww map world create <file-name> -> loads a world from the werewolf worlds folder");
                WerewolfUtil.sendCommandHelp(player, "/ww map world delete <world> -> unloads and deletes a world except lobby");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.map.world";
        }

        private static final class Create extends CommandNode {
            public Create() {
                super("create");
            }

            @Override
            public List<String> complete(CommandSender sender, Arguments arguments) {
                return (arguments.size() == 1) ? List.of("<file-name>") : Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (WerewolfGame.getMapManager().getWorldManager().getWorldFromName(arguments.get(0)) == null) {
                    WerewolfGame.getMapManager().getWorldManager().createWorld(arguments.get(0));
                    WerewolfUtil.sendCommandText(sender, "World loaded");
                } else {
                    WerewolfUtil.sendCommandError(sender, "World already existing");
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.world.create";
            }
        }

        private static final class Delete extends CommandNode {
            public Delete() {
                super("delete");
            }

            @Override
            public List<String> complete(CommandSender sender, Arguments arguments) {
                return (arguments.size() == 1) ? WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().stream().toList() : Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                if (WerewolfGame.getMapManager().getWorldManager().deleteWorld(arguments.find(0, "world", WerewolfGame.getMapManager().getWorldManager()::getWorldFromName))) {
                    WerewolfUtil.sendCommandText(sender, "World successfully deleted");
                } else {
                    WerewolfUtil.sendCommandError(sender, "World deletion failed");
                }
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.map.world.delete";
            }
        }
    }


    private static final class Select extends CommandNode {
        public Select() {
            super("select");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : Collections.emptyList();
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

    private static final class Create extends CommandNode {
        public Create() {
            super("create");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                return List.of("<map-name>");
            } else if (arguments.size() == 2) {
                return List.of("<world>");
            } else {
                return Collections.emptyList();
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 2) {
                org.bukkit.World world = arguments.find(1, "world", WerewolfGame.getMapManager().getWorldManager()::getWorldFromName);
                if (WerewolfGame.getMapManager().getMapFromName(arguments.get(0)) == null) {
                    WerewolfGame.getMapManager().createMap(arguments.get(0), world);
                    WerewolfUtil.sendCommandText(sender, "New map created :" + arguments.get(0));
                } else {
                    WerewolfUtil.sendCommandError(sender, "Map name already taken");
                }
            } else {
                WerewolfUtil.sendCommandError(sender, "Arguments length is incorrect");
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
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getMaps().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                WerewolfMap map = arguments.find(0, "map", WerewolfGame.getMapManager()::getMapFromName);
                WerewolfGame.getMapManager().deleteMap(map);
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

        public static final List<ArmorStand> armorStands = new ArrayList<>();

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton -> help");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton show -> summon armor stands to see spawn points");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton hide -> remove armor stands");
                WerewolfUtil.sendCommandHelp(player, "/ww map skeleton summon -> summon skeletons at every spawn point");
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
