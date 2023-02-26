package net.aesten.wwrpg.core;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.data.Role;
import net.aesten.wwrpg.data.WerewolfPlayerData;
import net.aesten.wwrpg.data.TeamsManager;
import net.aesten.wwrpg.items.registry.PlayerItem;
import net.aesten.wwrpg.items.registry.ItemManager;
import net.aesten.wwrpg.map.MapManager;
import net.aesten.wwrpg.map.WorldManager;
import net.aesten.wwrpg.shop.ShopManager;
import net.aesten.wwrpg.skeleton.SkeletonManager;
import net.aesten.wwrpg.tracker.Tracker;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.aesten.wwrpg.data.RolePool;
import net.aesten.wwrpg.map.WerewolfMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.*;

public class WerewolfGame {
    private static WerewolfGame instance = new WerewolfGame();
    private static final Listener listener = new ItemManager();
    private static final SkeletonManager skeletonManager = new SkeletonManager();
    private static final ShopManager shopManager = new ShopManager();
    private static final TeamsManager teamsManager = new TeamsManager();
    private static MapManager mapManager;
    private static String statusMessage;
    private final List<Player> participants;
    private final Map<UUID, WerewolfPlayerData> dataMap;
    private final Ticker ticker;
    private final RolePool pool;
    private final Tracker tracker = new Tracker();
    private WerewolfMap map;
    private boolean isPlaying;
    private boolean isNight;
    private final List<ArmorStand> displayNameArmorStands;

    public WerewolfGame() {
        this.participants = new ArrayList<>();
        this.dataMap = new HashMap<>();
        this.pool = new RolePool(1,0,0,0);
        this.ticker = new Ticker();
        this.isPlaying = false;
        this.isNight = false;
        this.displayNameArmorStands = new ArrayList<>();

        instance = this;
    }

    public WerewolfGame(WerewolfGame previousGame) {
        previousGame.participants.removeIf(p -> !Bukkit.getOnlinePlayers().contains(p));
        this.participants = previousGame.participants;
        this.dataMap = new HashMap<>();
        this.pool = previousGame.pool;
        this.ticker = new Ticker();
        this.map = previousGame.map;
        this.isPlaying = false;
        this.isNight = false;
        this.displayNameArmorStands = new ArrayList<>();

        instance = this;
    }

    public static WerewolfGame getInstance() {
        return instance;
    }

    public static SkeletonManager getSkeletonManager() {
        return skeletonManager;
    }

    public static ShopManager getShopManager() {
        return shopManager;
    }

    public static TeamsManager getTeamsManager() {
        return teamsManager;
    }

    public static MapManager getMapManager() {
        return mapManager;
    }

    public static String getStatusMessage() {
        return statusMessage;
    }

    public static void initMapManager() {
        mapManager = new MapManager(new WorldManager());
    }

    public Map<UUID, WerewolfPlayerData> getDataMap() {
        return dataMap;
    }

    public RolePool getPool() {
        return pool;
    }

    public Tracker getTracker() {
        return tracker;
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

    public static boolean isReady() {
        if (instance.participants.size() < 2) {
            statusMessage = "Not enough participants";
        }
        else if (instance.participants.size() > instance.map.getSkullLocations().size()) {
            statusMessage = "Too many players for selected map";
        }
        else if (!Bukkit.getOnlinePlayers().containsAll(instance.participants)) {
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
        Bukkit.getPluginManager().registerEvents(listener, WerewolfRpg.getPlugin());
        Bukkit.getPluginManager().registerEvents(skeletonManager, WerewolfRpg.getPlugin());

        int count = 0;
        List<Role> specialRoles = instance.pool.getRoles();

        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity.getType() == EntityType.DROPPED_ITEM) {
                entity.remove();
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
                    //select the role to attribute
                    Role role;
                    try {
                        role = specialRoles.get(count);
                    } catch (IndexOutOfBoundsException e) {
                        role = Role.VILLAGER;
                    }

                    //prepare stats tracker
                    instance.tracker.addPlayer(player).setRole(role);

                    //update skulls and put name
                    WerewolfUtil.updateSkull(instance.map.getWorld(),
                            instance.map.getSkullLocations().get(count),
                            player);
                    instance.displayNameArmorStands.add(
                            WerewolfUtil.summonNameTagArmorStand(instance.map.getWorld(),
                            instance.map.getSkullLocations().get(count),
                            new Vector(0, 0.4, 0),
                            player.getName())
                    );

                    //add player to correct team and set roles
                    instance.dataMap.put(player.getUniqueId(), new WerewolfPlayerData());
                    instance.dataMap.get(player.getUniqueId()).setRole(role);
                    teamsManager.registerPlayerRole(player, role);

                    //send role message to player
                    player.sendTitle(role.apparentRole().color + role.apparentRole().name,
                            ChatColor.GOLD + "GAME START", 2, 2, 40);
                    player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + ChatColor.RESET +
                            "You are a " + role.apparentRole().color + role.apparentRole().name);

                    //prepare player
                    player.setGameMode(GameMode.ADVENTURE);
                    player.getInventory().addItem(PlayerItem.SKELETON_PUNISHER.getItem());
                    player.getInventory().addItem(PlayerItem.EXQUISITE_MEAT.getItem());
                }
                else {
                    teamsManager.registerPlayerRole(player, Role.SPECTATOR);
                }
            }
        }

        if (teamsManager.getTeam(Role.WEREWOLF).getSize() > 1) {
            String werewolves = String.join(", ", teamsManager.getTeam(Role.WEREWOLF).getEntries());
            for (Player player : teamsManager.getFaction(Role.WEREWOLF).stream().map(Bukkit::getPlayer).toList()) {
                player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + ChatColor.RESET +
                        "The werewolves are " + Role.WEREWOLF.color + werewolves);
            }
        }

        instance.ticker.start();
    }

    public static void endGame() {
        if (teamsManager.getFaction(Role.VAMPIRE).size() != 0) {
            stop(Role.VAMPIRE);
        }
        else if (teamsManager.getFaction(Role.VILLAGER).size() == 0) {
            stop(Role.WEREWOLF);
        }
        else {
            stop(Role.VILLAGER);
        }
    }

    public static void interrupt() {
        stop(null);
    }

    private static String getEndString(Role role) {
        if (role == null) return ChatColor.YELLOW + "Game Cancelled";
        return role.color + role.name + " Victory!";
    }

    private static void stop(Role role) {
        //stop ticker
        instance.ticker.stop();

        //todo send data to database
        instance.tracker.setResults(role);

        //clear skulls todo check if skull operations work
        instance.map.getSkullLocations().forEach(v -> WerewolfUtil.resetSkull(instance.map.getWorld(), v));
        instance.displayNameArmorStands.forEach(ArmorStand::remove);

        //show roles to players
        WerewolfUtil.showMatchRoles();

        //reset all values
        instance = new WerewolfGame(instance);
        instance.map.getWorld().setTime(6000L);
        teamsManager.clear();
        HandlerList.unregisterAll(listener);
        HandlerList.unregisterAll(skeletonManager);

        //loop on players and remove other entities
        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.SKELETON) || entity.getType().equals(EntityType.DROPPED_ITEM)) {
                entity.remove();
            }
            else if (entity instanceof Player player) {
                WerewolfUtil.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
                player.getInventory().clear();
                player.sendTitle(getEndString(role), ChatColor.GOLD + "GAME END", 2, 2, 40);
                player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + getEndString(role));
                player.setGameMode(GameMode.SPECTATOR);
            }
        }

        //gather players back to spawn
        WerewolfUtil.runDelayedTask(100, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(instance.getMap().getMapSpawn());
                player.setGameMode(GameMode.ADVENTURE);
            }
        });
    }
}
