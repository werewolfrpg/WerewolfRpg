package net.aesten.wwrpg.commands.subcommands;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class WorldCommand extends CommandNode {
    public WorldCommand() {
        super("world",
                new List(),
                new Create(),
                new Delete()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww world -> help");
            WerewolfUtil.sendHelpText(player, "/ww world list -> list all worlds of the server");
            WerewolfUtil.sendHelpText(player, "/ww world create <file-name> -> loads a world from the werewolf worlds folder");
            WerewolfUtil.sendHelpText(player, "/ww world delete <world> -> unloads and deletes a world except lobby");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.world";
    }

    private static final class List extends CommandNode {
        public List() {
            super("list");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfUtil.sendPluginText(sender, "Loaded worlds:");
            WerewolfGame.getMapManager().getWorldManager().getWorlds().keySet().forEach(s -> WerewolfUtil.sendPluginText(sender, s));
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.world.list";
        }
    }


    private static final class Create extends CommandNode {
        public Create() {
            super("create");
        }

        @Override
        public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
            return (arguments.size() == 1) ? java.util.List.of("<file-name>") : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (WerewolfGame.getMapManager().getWorldManager().getWorldFromName(arguments.get(0)) == null) {
                WerewolfGame.getMapManager().getWorldManager().createWorld(arguments.get(0));
                WerewolfUtil.sendPluginText(sender, "World loaded");
            } else {
                WerewolfUtil.sendErrorText(sender, "World already existing");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.world.create";
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
            if (WerewolfGame.getMapManager().getWorldManager().deleteWorld(arguments.find(0, "world", WerewolfGame.getMapManager().getWorldManager()::getWorldFromName))) {
                WerewolfUtil.sendPluginText(sender, "World successfully deleted");
            } else {
                WerewolfUtil.sendErrorText(sender, "World deletion failed");
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.world.delete";
        }
    }
}
