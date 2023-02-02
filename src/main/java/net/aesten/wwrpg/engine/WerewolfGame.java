package net.aesten.wwrpg.engine;

import net.aesten.wwrpg.WerewolfUtil;
import net.aesten.wwrpg.config.RolePool;
import net.aesten.wwrpg.items.ItemRegistry;
import net.aesten.wwrpg.wwrpg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

import java.util.*;

public class WerewolfGame {
    private static WerewolfGame instance;
    private static String statusMessage;
    private final List<Player> participants;
    private final Map<UUID, WerewolfPlayerData> dataMap;
    private final Map<Role, List<UUID>> teamsMap;
    private final RolePool pool;
    private WerewolfMap map;
    private final Ticker ticker;
    private boolean isPlaying;
    private boolean isNight;

    public WerewolfGame(WerewolfMap map) {
        this.participants = new ArrayList<>();
        this.dataMap = new HashMap<>();
        this.teamsMap = new HashMap<>();
        this.pool = new RolePool(0,0,0,0);
        this.ticker = new Ticker();
        this.map = map;
        this.isPlaying = false;
        this.isNight = false;

        for (Role role : Role.values()) {
            this.teamsMap.put(role, new ArrayList<>());
        }

        instance = this;
    }

    public WerewolfGame(WerewolfGame previousGame) {
        this.participants = previousGame.participants;
        this.dataMap = previousGame.dataMap;
        this.teamsMap = previousGame.teamsMap;
        this.pool = previousGame.pool;
        this.ticker = new Ticker();
        this.map = previousGame.map;
        this.isPlaying = false;
        this.isNight = false;

        for (Role role : Role.values()) {
            this.teamsMap.put(role, new ArrayList<>());
        }

        instance = this;
    }

    public static WerewolfGame getInstance() {
        return instance;
    }

    public static String getStatusMessage() {
        return statusMessage;
    }

    public List<UUID> getFaction(Role role) {
        return teamsMap.get(role);
    }

    public boolean isNight() {
        return isNight;
    }

    public void switchDayNight() {
        isNight = !isNight;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public WerewolfMap getMap() {
        return map;
    }

    public void setMap(WerewolfMap map) {
        this.map = map;
    }

    public List<Player> getParticipants() {
        return participants;
    }

    public boolean isParticipant(Player player) {
        return participants.contains(player);
    }

    public static void init() {
        instance = new WerewolfGame(WerewolfMaps.getLobby); //todo
    }

    public static boolean isReady() {
        if (instance.participants.size() < 2) {
            statusMessage = "Not enough participants";
        }
        else if (instance.participants.size() > instance.map.getSignPostLocations().size()) {
            statusMessage = "Too many players for selected map";
        }
        else if (Bukkit.getOnlinePlayers().containsAll(instance.participants)) {
            statusMessage = "Some participants are not online";
        }
        else if (instance.pool.getWerewolfNumber() == 0) {
            statusMessage = "You need at least 1 werewolf to start a game";
        }
        else if (instance.pool.getTotalSpecialRoles() >= instance.participants.size()) {
            statusMessage = "Too many special roles selected";
        }
        else {
            statusMessage = "Game is ready";
            return true;
        }
        return false;
    }


    public static void start() {
        instance.isPlaying = true;
        instance.map.getWorld().setTime(6000L);

        int count = 0;
        List<Role> specialRoles = instance.pool.getRoles();

        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity instanceof Item item) {
                item.remove();
            }
            else if (entity instanceof Player player) {
                WerewolfUtil.removeAllPotionEffectsFromPlayer(player);
                instance.ticker.addPlayer(player);
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                player.teleport(instance.map.getMapSpawn());
                player.setHealth(40);
                player.setFoodLevel(20);
                player.setSaturation(20);

                if (instance.participants.contains(player)) {
                    //Select the role to attribute
                    Role role;
                    try {
                        role = specialRoles.get(count);
                    } catch (IndexOutOfBoundsException e) {
                        role = Role.VILLAGER;
                    }

                    //Update sign post
                    WerewolfUtil.updateSignPost(instance.map.getWorld(),
                            instance.map.getSignPostLocations().get(count),
                            ChatColor.GRAY + "Player:",
                            player.getName(),
                            ChatColor.GRAY + "Click to divinate");

                    //Add player to correct team and set roles
                    instance.teamsMap.get(role).add(player.getUniqueId());
                    WerewolfPlayerData.getDataMap().put(player.getUniqueId(), new WerewolfPlayerData());
                    WerewolfPlayerData.getData(player.getUniqueId()).setRole(role);
                    WerewolfTeams.getTeam(role).addEntry(player.getName());

                    //Send role message to player
                    player.sendTitle(role.apparentRole().color + role.apparentRole().name,
                            ChatColor.GOLD + "GAME START", 2, 2, 40);
                    player.sendMessage(wwrpg.COLOR + wwrpg.LOG + ChatColor.RESET +
                            "You are a " + role.apparentRole().color + role.apparentRole().name);

                    //Prepare player
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().addItem(ItemRegistry.SKELETON_PUNISHER.getItemStack());
                    player.getInventory().addItem(ItemRegistry.EXQUISITE_MEAT.getItemStack());
                }
            }
        }

        if (WerewolfTeams.getTeam(Role.WEREWOLF).getSize() > 1) {
            String werewolves = String.join(", ", WerewolfTeams.getTeam(Role.WEREWOLF).getEntries());
            for (Player player : instance.teamsMap.get(Role.WEREWOLF).stream().map(Bukkit::getPlayer).toList()) {
                player.sendMessage(wwrpg.COLOR + wwrpg.LOG + ChatColor.RESET +
                        "The werewolves are " + Role.WEREWOLF.color + werewolves);
            }
        }

        instance.ticker.start();
    }

    public static void endGame() {
        if (instance.getFaction(Role.VAMPIRE).size() != 0) {
            stop(ChatColor.LIGHT_PURPLE + "Vampire Victory!");
        }
        else if (instance.getFaction(Role.VILLAGER).size() == 0) {
            stop(ChatColor.DARK_RED + "Werewolf Victory!");
        }
        else {
            stop(ChatColor.GREEN + "Villager Victory!");
        }
    }

    public static void interrupt() {
        stop(ChatColor.YELLOW + "Game Cancelled");
    }

    private static void stop(String endReason) {
        //todo send data to database
        WerewolfUtil.showMatchRoles();
        //todo erase sign posts
        instance.isPlaying = false;
        instance.isNight = false;
        instance.dataMap.clear();
        instance.map.getWorld().setTime(6000L);
        WerewolfTeams.clear();

        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.SKELETON) || entity.getType().equals(EntityType.DROPPED_ITEM)) {
                entity.remove();
            }
            else if (entity instanceof Player player) {
                player.getInventory().clear();
                player.sendTitle(comment, ChatColor.GOLD + "GAME END", 2, 2, 40);
                player.sendMessage(wwrpg.color + "[WWRPG]: " + comment);
            }
        }

    }


}
