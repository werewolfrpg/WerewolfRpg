package net.aesten.werewolfmc.plugin.commands.subcommands;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.command.CommandNode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.List;

public class SummonCommand extends CommandNode {
    public SummonCommand() {
        super("summon");
    }

    @Override
    public List<String> complete(CommandSender sender, Arguments arguments) {
        return (arguments.size() == 1) ? List.of("basic_skeleton", "lucky_skeleton", "special_skeleton") : Collections.emptyList();
    }

    @Override
    public void execute(CommandSender sender, Arguments arguments) {
        if (sender instanceof Player player) {
            Location location = player.getLocation().clone();
            Vector vector = location.toVector();
            World world = location.getWorld();
            assert world != null;
            switch (arguments.get(0)) {
                case "basic_skeleton": WerewolfGame.getSkeletonManager().summonBasicSkeleton(world, vector);
                case "lucky_skeleton": WerewolfGame.getSkeletonManager().summonLuckySkeleton(world, vector);
                case "special_skeleton": WerewolfGame.getSkeletonManager().summonSpecialSkeleton(world, vector);
                default: WerewolfUtil.sendErrorText(sender, "Invalid summon attempt");
            }
        }
    }

    @Override
    public String getPermission() {
        return "werewolf.cmd.ww.summon";
    }
}
