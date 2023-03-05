package net.aesten.wwrpg.commands.subcommands;

import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.RolePool;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;

public class SettingCommand extends CommandNode {
    public SettingCommand() {
        super("setting",
                new Roles(),
                new UpdateShops()
                );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww setting -> help");
            WerewolfUtil.sendHelpText(player, "/ww setting update-shops -> update shop costs with config values");
            WerewolfUtil.sendHelpText(player, "/ww setting roles [...] -> change the number of each role");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.setting";
    }

    private static final class UpdateShops extends CommandNode {
        public UpdateShops() {
            super("update-shops");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            WerewolfGame.getShopManager().updatePrices(WerewolfGame.getInstance().getMap().getWorld());
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.setting.update-shops";
        }
    }

    private static final class Roles extends CommandNode {
        public Roles() {
            super("roles",
                    new List(),
                    new Reset(),
                    new Werewolf(),
                    new Traitor(),
                    new Vampire(),
                    new Possessed()
                );
        }

        private void help(CommandSender sender) {
            if (sender instanceof Player player) {
                WerewolfUtil.sendHelpText(player, "/ww setting roles -> help");
                WerewolfUtil.sendHelpText(player, "/ww setting roles list -> list the current configuration of roles");
                WerewolfUtil.sendHelpText(player, "/ww setting roles reset -> reset to only 1 werewolf");
                WerewolfUtil.sendHelpText(player, "/ww setting roles werewolf <int> -> change the number of werewolves");
                WerewolfUtil.sendHelpText(player, "/ww setting roles traitor <int> -> change the number of traitors");
                WerewolfUtil.sendHelpText(player, "/ww setting roles vampire <int> -> change the number of vampires");
                WerewolfUtil.sendHelpText(player, "/ww setting roles possessed <int> -> change the number of possessed");
            }
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            help(sender);
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.setting.roles";
        }

        private static final class List extends CommandNode {
            public List() {
                super("list");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                RolePool pool = WerewolfGame.getInstance().getPool();
                WerewolfUtil.sendPluginText(sender, "Current role configuration:");
                WerewolfUtil.sendPluginText(sender, "Werewolves: " + pool.getWerewolfNumber(), Role.WEREWOLF.color);
                WerewolfUtil.sendPluginText(sender, "Traitors: " + pool.getTraitorNumber(), Role.TRAITOR.color);
                WerewolfUtil.sendPluginText(sender, "Vampires: " + pool.getVampireNumber(), Role.VAMPIRE.color);
                WerewolfUtil.sendPluginText(sender, "Possessed: " + pool.getPossessedNumber(), Role.POSSESSED.color);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.list";
            }
        }

        private static final class Reset extends CommandNode {
            public Reset() {
                super("reset");
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                RolePool pool = WerewolfGame.getInstance().getPool();
                pool.setWerewolfNumber(1);
                pool.setTraitorNumber(0);
                pool.setVampireNumber(0);
                pool.setPossessedNumber(0);
                WerewolfUtil.sendPluginText(sender, "Roles are reset");
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.reset";
            }
        }

        private static final class Werewolf extends CommandNode {
            public Werewolf() {
                super("werewolf");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer werewolfNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setWerewolfNumber(werewolfNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.WEREWOLF.color + Role.WEREWOLF.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + werewolfNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.werewolf";
            }
        }

        private static final class Traitor extends CommandNode {
            public Traitor() {
                super("traitor");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer traitorNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setTraitorNumber(traitorNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.TRAITOR.color + Role.TRAITOR.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + traitorNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.traitor";
            }
        }

        private static final class Vampire extends CommandNode {
            public Vampire() {
                super("vampire");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer vampireNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setVampireNumber(vampireNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.VAMPIRE.color + Role.VAMPIRE.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + vampireNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.vampire";
            }
        }

        private static final class Possessed extends CommandNode {
            public Possessed() {
                super("possessed");
            }

            @Override
            public java.util.List<String> complete(CommandSender sender, Arguments arguments) {
                if (arguments.size() == 1) return java.util.List.of("<int>");
                else return Collections.emptyList();
            }

            @Override
            public void execute(CommandSender sender, Arguments arguments) {
                Integer possessedNumber = arguments.find(0, "int", Integer::parseInt);
                WerewolfGame.getInstance().getPool().setPossessedNumber(possessedNumber);
                WerewolfUtil.sendPluginText(sender, "Changed " + Role.POSSESSED.color + Role.POSSESSED.name + ChatColor.AQUA + " number to " + ChatColor.GOLD + possessedNumber);
            }

            @Override
            public String getPermission() {
                return "wwrpg.cmd.ww.setting.roles.possessed";
            }
        }
    }
}
