package net.aesten.wwrpg.commands.admin;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UtilCommand extends CommandNode {
    public UtilCommand() {
        super("util",
                new Spawn(),
                new Heal(),
                new Role(),
                new Adventure(),
                new Spectator()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendCommandHelp(player, "/ww util -> help");
            WerewolfUtil.sendCommandHelp(player, "/ww util spawn -> teleport to playing map spawn");
            WerewolfUtil.sendCommandHelp(player, "/ww util heal -> heal and fill hunger");
            WerewolfUtil.sendCommandHelp(player, "/ww util role -> get current role in-game");
            WerewolfUtil.sendCommandHelp(player, "/ww util spectator -> switch to spectator mode");
            WerewolfUtil.sendCommandHelp(player, "/ww util adventure -> switch to adventure mode");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.util";
    }

    private static final class Spawn extends CommandNode {
        public Spawn() {
            super("spawn");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                if (!WerewolfGame.getInstance().isPlaying()) {
                    player.teleport(WerewolfGame.getInstance().getMap().getMapSpawn());
                } else {
                    WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.spawn";
        }
    }

    private static final class Heal extends CommandNode {
        public Heal() {
            super("heal");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                if (!WerewolfGame.getInstance().isPlaying()) {
                    player.setHealth(40d);
                    player.setFoodLevel(20);
                    player.setSaturation(20f);
                } else {
                    WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.heal";
        }
    }

    private static final class Role extends CommandNode {
        public Role() {
            super("role");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                if (WerewolfGame.getInstance().isPlaying()) {
                    net.aesten.wwrpg.data.Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
                    WerewolfUtil.sendCommandText(sender, "Your role is: " + role.color + role.name);
                } else {
                    WerewolfUtil.sendCommandError(sender, "This command only works during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.role";
        }
    }

    private static final class Adventure extends CommandNode {
        public Adventure() {
            super("adventure");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                if (!WerewolfGame.getInstance().isPlaying()) {
                    player.setGameMode(GameMode.ADVENTURE);
                } else {
                    WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.adventure";
        }
    }

    private static final class Spectator extends CommandNode {
        public Spectator() {
            super("spectator");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                if (!WerewolfGame.getInstance().isPlaying()) {
                    player.setGameMode(GameMode.ADVENTURE);
                } else {
                    WerewolfUtil.sendCommandError(sender, "You cannot use this command during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.spectator";
        }
    }
}
