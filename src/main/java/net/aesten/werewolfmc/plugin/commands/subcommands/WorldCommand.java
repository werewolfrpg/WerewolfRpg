package net.aesten.werewolfmc.plugin.commands.subcommands;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.map.WorldManager;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class WorldCommand extends CommandNode {
    public WorldCommand() {
        super("world",
                new List(),
                new Load(),
                new Delete()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww world -> help");
            WerewolfUtil.sendHelpText(player, "/ww world list -> list all worlds of the server");
            WerewolfUtil.sendHelpText(player, "/ww world load <file-name> -> loads a world from the werewolf worlds folder");
            WerewolfUtil.sendHelpText(player, "/ww world delete <world> -> unloads and deletes a world except lobby");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    private static final class List extends CommandNode {
        public List() {
            super("list");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfUtil.sendPluginText(sender, "Loaded worlds:");
            WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().forEach(s -> WerewolfUtil.sendPluginText(sender, s, ChatColor.LIGHT_PURPLE));
        }

        @Override
        public String getPermission() {
            return "werewolf.cmd.ww.world.list";
        }
    }


    private static final class Load extends CommandNode {
        public Load() {
            super("load");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? java.util.List.of("<file-name>") : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (arguments.size() == 1) {
                WorldManager manager = WerewolfGame.getMapManager().getWorldManager();
                if (manager.worldContainerExists(arguments.get(0))) {
                    if (manager.getWorldFromName(arguments.get(0)) == null) {
                        manager.loadWorld(arguments.get(0));
                        WerewolfUtil.sendPluginText(sender, "World loaded");
                    } else {
                        WerewolfUtil.sendErrorText(sender, "World already existing");
                    }
                } else {
                    WerewolfUtil.sendErrorText(sender, "No such folder found");
                }
            } else {
                WerewolfUtil.sendErrorText(sender, "Arguments length is incorrect");
            }
        }

        @Override
        public String getPermission() {
            return "werewolf.cmd.ww.world.load";
        }
    }

    private static final class Delete extends CommandNode {
        public Delete() {
            super("delete");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getMapManager().getWorldManager().deleteWorld(arguments.get(0))) {
                WerewolfUtil.sendPluginText(sender, "World successfully deleted");
            } else {
                WerewolfUtil.sendErrorText(sender, "World deletion failed");
            }
        }

        @Override
        public String getPermission() {
            return "werewolf.cmd.ww.world.delete";
        }
    }
}
