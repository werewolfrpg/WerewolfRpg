package net.aesten.werewolfmc.plugin.data;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.*;

public class TeamsManager {
    private final Map<Faction, List<Player>> factions = new HashMap<>();
    private final List<PlayerData> playerData = new ArrayList<>();

    public TeamsManager() {
        init();
    }

    public void clear() {
        Arrays.stream(Role.values()).forEach(role -> role.getTeam().getEntries().forEach(role.getTeam()::removeEntry));
        factions.clear();
        playerData.clear();
        init();
    }

    private void init() {
        factions.put(Faction.VILLAGER, new ArrayList<>());
        factions.put(Faction.WEREWOLF, new ArrayList<>());
        factions.put(Faction.OTHER, new ArrayList<>());
    }

    public void registerPlayerRole(Player player, Role role) {
        playerData.add(new PlayerData(player, role));
        role.getTeam().addEntry(player.getName());
        if (role.isCountInFaction()) factions.get(role.getFaction()).add(player);
    }

    public void switchRole(Player player, Role newRole) {
        PlayerData playerData = getPlayerData(player);
        if (playerData == null) return;
        WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).setRole(newRole);
        playerData.role = newRole;
        Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
        role.getTeam().removeEntry(player.getName());
        newRole.getTeam().addEntry(player.getName());
        if (role.isCountInFaction()) factions.get(role.getFaction()).remove(player);
        if (newRole.isCountInFaction()) factions.get(newRole.getFaction()).add(player);
    }

    public void playerDied(Player player) {
        Role role = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).getRole();
        if (role.isCountInFaction()) factions.get(role.getFaction()).remove(player);
    }

    public int getFactionSize(Faction faction) {
        return factions.get(faction).size();
    }

    public void unregisterTeams() {
        Arrays.stream(Role.values()).map(Role::getTeam).forEach(Team::unregister);
    }

    public Map<Faction, List<PlayerData>> getData() {
        Map<Faction, List<PlayerData>> map = new LinkedHashMap<>();
        map.put(Faction.VILLAGER, new ArrayList<>());
        map.put(Faction.WEREWOLF, new ArrayList<>());
        map.put(Faction.OTHER, new ArrayList<>());
        playerData.forEach(data -> map.get(data.getRole().getFaction()).add(data));
        return map;
    }

    public PlayerData getPlayerData(Player player) {
        return playerData.stream().filter(data -> data.getUuid().equals(player.getUniqueId())).findAny().orElse(null);
    }

    public static final class PlayerData {
        private final UUID uuid;
        private final String name;
        private Role role;

        public PlayerData(Player player, Role role) {
            this.uuid = player.getUniqueId();
            this.name = player.getName();
            this.role = role;
        }

        public UUID getUuid() {
            return uuid;
        }

        public String getName() {
            return name;
        }

        public Role getRole() {
            return role;
        }
    }
}
