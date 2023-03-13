package net.aesten.werewolfrpg.map;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MapEditingHelper {
    private final Map<Player, WerewolfMap> selectedMapsCacheMap = new HashMap<>();

    public void selectMap(Player player, WerewolfMap map) {
        selectedMapsCacheMap.put(player, map);
    }

    public boolean hasSelectedMap(Player player) {
        return selectedMapsCacheMap.containsKey(player);
    }

    public WerewolfMap getSelectedMap(Player player) {
        return selectedMapsCacheMap.get(player);
    }

    public boolean addOrElseRemove(List<Vector> list, Vector vector) {
        if (list.contains(vector)) {
            list.remove(vector);
            return false;
        }
        else {
            list.add(vector);
            return true;
        }
    }
}
