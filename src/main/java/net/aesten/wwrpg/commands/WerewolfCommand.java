package net.aesten.wwrpg.commands;

import net.azalealibrary.command.CommandNode;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;

public class WerewolfCommand extends CommandNode {
    private static final Map<Player, List<Vector>> skullLocationCacheMap = new HashMap<>();
    private static final Map<Player, List<Vector>> skeletonLocationCacheMap = new HashMap<>();



    public WerewolfCommand() {
        super("/ww" /*, children...*/);
    }



    public static Map<Player, List<Vector>> getSkullLocationCacheMap() {
        return skullLocationCacheMap;
    }

    public static Map<Player, List<Vector>> getSkeletonLocationCacheMap() {
        return skeletonLocationCacheMap;
    }

    public static void addSkeletonSpawn(Player player, Vector vector) {
        skeletonLocationCacheMap.computeIfAbsent(player, k -> new ArrayList<>(Collections.singleton(vector)));
    }

    public static void addSkullLocation(Player player, Vector vector) {
        skullLocationCacheMap.computeIfAbsent(player, k -> new ArrayList<>(Collections.singleton(vector)));
    }
}
