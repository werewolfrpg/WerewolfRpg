package net.aesten.wwrpg.commands.subcommands;

import net.aesten.wwrpg.items.registry.AdminItem;
import net.aesten.wwrpg.items.registry.PlayerItem;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemCommand extends CommandNode {
    public ItemCommand() {
        super("item",
                new Get(),
                new Tools()
        );
    }

    private void help(CommandSender sender) {
        if (sender instanceof Player player) {
            WerewolfUtil.sendHelpText(player, "/ww item -> help");
            WerewolfUtil.sendHelpText(player, "/ww item get <item> -> get the specified minigame item");
            WerewolfUtil.sendHelpText(player, "/ww item tools -> get all the map editing tools");
        }
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        help(sender);
    }

    @Override
    public String getPermission() {
        return "wwrpg.cmd.ww.item";
    }

    private static final class Get extends CommandNode {
        public Get() {
            super("get");
        }

        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return arguments.size() == 1 ? PlayerItem.getRegistry().keySet().stream().toList() : Collections.emptyList();
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                player.getInventory().addItem(PlayerItem.getItemFromId(arguments.get(0)).getItem());
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.item.get";
        }
    }

    private static final class Tools extends CommandNode {
        public Tools() {
            super("tools");
        }

        @Override
        public void execute(CommandSender sender, Arguments arguments) {
            if (sender instanceof Player player) {
                Inventory inv = player.getInventory();
                ItemStack none = new ItemStack(Material.AIR, 1);
                inv.setItem(0, AdminItem.SKULL_WAND.getItem());
                inv.setItem(1, AdminItem.PLACEHOLDER_SKULL.getItem());
                inv.setItem(2, none);
                inv.setItem(3, AdminItem.SKELETON_SPAWN_POINT_WAND.getItem());
                inv.setItem(4, none);
                inv.setItem(5, AdminItem.BASIC_VILLAGER_SUMMON_WAND.getItem());
                inv.setItem(6, AdminItem.SPECIAL_VILLAGER_SUMMON_WAND.getItem());
                inv.setItem(7, new ItemStack(Material.ARMOR_STAND, 1));
                inv.setItem(8, none);
            }
        }

        @Override
        public String getPermission() {
            return "wwrpg.cmd.ww.item.tools";
        }
    }
}
