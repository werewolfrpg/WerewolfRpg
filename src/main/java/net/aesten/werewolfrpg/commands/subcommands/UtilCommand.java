package net.aesten.werewolfrpg.commands.subcommands;

import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
            WerewolfUtil.sendHelpText(player, "/ww util -> help");
            WerewolfUtil.sendHelpText(player, "/ww util spawn -> teleport to playing map spawn");
            WerewolfUtil.sendHelpText(player, "/ww util heal -> heal and fill hunger");
            WerewolfUtil.sendHelpText(player, "/ww util role -> get current role in-game");
            WerewolfUtil.sendHelpText(player, "/ww util spectator -> switch to spectator mode");
            WerewolfUtil.sendHelpText(player, "/ww util adventure -> switch to adventure mode");
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
                    WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
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
                    player.setHealth(20d);
                    player.setFoodLevel(20);
                    player.setSaturation(20f);
                } else {
                    WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
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
                    net.aesten.werewolfrpg.data.Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
                    WerewolfUtil.sendPluginText(sender, "Your role is: " + role.color + role.name);
                } else {
                    WerewolfUtil.sendErrorText(sender, "This command only works during a game");
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
                    WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
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
                    player.setGameMode(GameMode.SPECTATOR);
                } else {
                    WerewolfUtil.sendErrorText(sender, "You cannot use this command during a game");
                }
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.util.spectator";
        }
    }
}
