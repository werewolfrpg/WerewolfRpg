package net.aesten.werewolfrpg.core;

import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.data.Role;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.data.TeamsManager;
import net.aesten.werewolfrpg.items.registry.PlayerItem;
import net.aesten.werewolfrpg.items.registry.ItemManager;
import net.aesten.werewolfrpg.map.MapManager;
import net.aesten.werewolfrpg.map.WorldManager;
import net.aesten.werewolfrpg.shop.ShopManager;
import net.aesten.werewolfrpg.skeleton.SkeletonManager;
import net.aesten.werewolfrpg.tracker.Tracker;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import net.aesten.werewolfrpg.data.RolePool;
import net.aesten.werewolfrpg.map.WerewolfMap;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.sql.Timestamp;

public class WerewolfGame {
    private static WerewolfGame instance = new WerewolfGame();
    private static final Listener listener = new ItemManager();
    private static final SkeletonManager skeletonManager = new SkeletonManager();
    private static final ShopManager shopManager = new ShopManager();
    private static final TeamsManager teamsManager = new TeamsManager();
    private static MapManager mapManager;
    private static String statusMessage;
    private final String matchId;
    private final List<Player> participants;
    private final Map<UUID, WerewolfPlayerData> dataMap;
    private final Ticker ticker;
    private final RolePool pool;
    private final Tracker tracker = new Tracker();
    private WerewolfMap map;
    private boolean isPlaying;
    private boolean isNight;
    private Timestamp startTime;
    private Timestamp endTime;
    private final List<ArmorStand> displayNameArmorStands;

    public WerewolfGame() {
        this.matchId = UUID.randomUUID().toString();
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
        this.matchId = UUID.randomUUID().toString();
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
        WerewolfRpg.logConsole("Initializing worlds and maps");
        mapManager = new MapManager(new WorldManager());
    }

    public String getMatchId() {
        return matchId;
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

    public Timestamp getStartTime() {
        return startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public WerewolfMap getMap() {
        return map;
    }

    public void setMap(WerewolfMap map) {
        if (map != null) this.map = map;
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
        } else if (instance.participants.size() > instance.map.getSkullLocations().size()) {
            statusMessage = "Too many players for selected map";
        } else if (!instance.participants.stream().allMatch(Player::isOnline)) {
            statusMessage = "Some participants are not online";
        } else if (instance.map == null || instance.map.equals(mapManager.getMapFromName("wwrpg-maps" + File.separator + "lobby"))) {
            statusMessage = "Selected map is not valid";
        } else if (instance.pool.getWerewolfNumber() == 0) {
            statusMessage = "You need at least 1 werewolf to start a game";
        } else if (instance.pool.getTotalSpecialRoles() >= instance.participants.size()) {
            statusMessage = "Too many special roles selected";
        } else {
            statusMessage = "Game is ready";
            return true;
        }
        return false;
    }

    public static void start() {
        //set status and register listeners
        instance.isPlaying = true;
        Bukkit.getPluginManager().registerEvents(listener, WerewolfRpg.getPlugin());
        Bukkit.getPluginManager().registerEvents(skeletonManager, WerewolfRpg.getPlugin());

        //prepare map
        World world = instance.map.getWorld();
        world.setTime(6000L);
        world.getWorldBorder().setCenter(instance.map.getBorderCenter().toLocation(world));
        world.getWorldBorder().setSize(instance.map.getBorderSize());

        //role pool
        int count = 0;
        List<Role> specialRoles = instance.pool.getRoles(instance.participants.size());

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
                    WerewolfUtil.updateSkull(
                            instance.map.getWorld(),
                            instance.map.getSkullLocations().get(count),
                            player
                    );
                    instance.displayNameArmorStands.add(
                            WerewolfUtil.summonNameTagArmorStand(
                                    instance.map.getWorld(),
                                    instance.map.getSkullLocations().get(count),
                                    new Vector(0.5, 0, 0.5),
                                    player.getName()
                            )
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
                    player.getInventory().addItem(PlayerItem.MUTER.getItem());

                    //increment
                    count++;
                } else {
                    teamsManager.addPlayerToSpectator(player);
                }
            }

        }

        if (teamsManager.getTeam(Role.WEREWOLF).getSize() > 1) {
            String werewolves = String.join(", ", teamsManager.getTeam(Role.WEREWOLF).getEntries());
            for (Player player : teamsManager.getTeam(Role.WEREWOLF).getEntries().stream().map(Bukkit::getPlayerExact).toList()) {
                player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + ChatColor.RESET +
                        "The werewolves are " + Role.WEREWOLF.color + werewolves);
            }
        }

        instance.ticker.start();
        instance.startTime = new Timestamp(System.currentTimeMillis());
    }

    private static void stop(Role role) {
        //stop ticker
        instance.ticker.stop();
        instance.endTime = new Timestamp(System.currentTimeMillis());

        //prepare and send stats
        instance.tracker.setResults(role);
        instance.tracker.sendDataToDatabase(instance, role);
        if (WerewolfRpg.getBot() != null && WerewolfRpg.getBot().getCurrentSession() != null) {
            instance.tracker.logMatchResult(instance, role);
        }

        //clear skulls
        instance.map.getSkullLocations().forEach(v -> WerewolfUtil.resetSkull(instance.map.getWorld(), v));
        instance.displayNameArmorStands.forEach(ArmorStand::remove);

        //get role list (before resetting)
        String rolesList = getMatchText();

        //reset all values
        instance = new WerewolfGame(instance);
        World world = instance.map.getWorld();
        world.setTime(6000L);
        world.getWorldBorder().setCenter(world.getSpawnLocation());
        world.getWorldBorder().setSize(1000000d);
        teamsManager.clear();
        HandlerList.unregisterAll(listener);
        HandlerList.unregisterAll(skeletonManager);

        //get voice channel to unmute
        VoiceChannel vc = null;
        if (WerewolfRpg.getBot() != null && WerewolfRpg.getBot().getCurrentSession() != null) {
            vc = WerewolfRpg.getBot().getCurrentSession().getVc();
        }

        //loop on players and remove other entities
        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.SKELETON) || entity.getType().equals(EntityType.DROPPED_ITEM)) {
                entity.remove();
            }
            else if (entity instanceof Player player) {
                WerewolfUtil.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
                player.sendMessage(rolesList);
                player.getInventory().clear();
                player.sendTitle(getEndString(role), ChatColor.GOLD + "GAME END", 2, 2, 40);
                player.sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + getEndString(role));
                player.setGameMode(GameMode.SPECTATOR);
                player.setHealth(40);
                player.setFoodLevel(20);
                player.setSaturation(20);

                if (vc != null) {
                    List<String> str = QueryManager.getDiscordIdsOfPlayer(player.getUniqueId().toString());
                    List<Member> dcMember = vc.getMembers().stream().filter(member -> str.contains(member.getId())).toList();
                    dcMember.forEach(member -> member.mute(false).queue());
                }
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

    public static void interrupt() {
        stop(null);
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

    private static String getEndString(Role role) {
        if (role == null) return ChatColor.YELLOW + "Game Cancelled";
        return role.color + role.name + " Victory!";
    }

    private static String getMatchText() {
        TeamsManager teamsManager = WerewolfGame.getTeamsManager();
        Set<String> villagers = teamsManager.getTeam(Role.VILLAGER).getEntries();
        Set<String> werewolves = teamsManager.getTeam(Role.WEREWOLF).getEntries();
        Set<String> traitors = teamsManager.getTeam(Role.TRAITOR).getEntries();
        Set<String> vampires = teamsManager.getTeam(Role.VAMPIRE).getEntries();
        Set<String> possessed = teamsManager.getTeam(Role.POSSESSED).getEntries();

        StringBuilder builder = new StringBuilder();

        builder.append(ChatColor.AQUA + "======WWRPG Match Role======" + "\n");
        builder.append(ChatColor.GREEN + "Villagers:" + "\n");
        builder.append("> " + String.join(", ", villagers) + "\n");
        builder.append(ChatColor.DARK_RED + "Werewolves:" + "\n");
        builder.append("> " + String.join(", ", werewolves) + "\n");

        if (traitors.size() > 0) {
            builder.append(ChatColor.LIGHT_PURPLE + "Traitors:" + "\n");
            builder.append("> " + String.join(", ", traitors) + "\n");
        }
        if (traitors.size() > 0) {
            builder.append(ChatColor.DARK_PURPLE + "Vampire:" + "\n");
            builder.append("> " + String.join(", ", vampires) + "\n");
        }
        if (traitors.size() > 0) {
            builder.append(ChatColor.YELLOW + "Possessed:" + "\n");
            builder.append("> " + String.join(", ", possessed) + "\n");
        }

        builder.append(ChatColor.AQUA + "======WWRPG Match Role======" + "\n");


        return builder.toString();
    }
}
