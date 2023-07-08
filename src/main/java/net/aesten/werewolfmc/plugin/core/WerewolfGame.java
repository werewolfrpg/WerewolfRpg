package net.aesten.werewolfmc.plugin.core;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.data.*;
import net.aesten.werewolfmc.plugin.items.registry.PlayerItem;
import net.aesten.werewolfmc.plugin.items.registry.ItemManager;
import net.aesten.werewolfmc.plugin.items.registry.player.WerewolfTrap;
import net.aesten.werewolfmc.plugin.map.MapManager;
import net.aesten.werewolfmc.plugin.map.WorldManager;
import net.aesten.werewolfmc.plugin.shop.ShopManager;
import net.aesten.werewolfmc.plugin.skeleton.SkeletonManager;
import net.aesten.werewolfmc.plugin.statistics.ScoreManager;
import net.aesten.werewolfmc.plugin.statistics.Tracker;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;
import java.sql.Timestamp;

public class WerewolfGame {
    private static WerewolfGame instance = new WerewolfGame();
    private static final Listener listener = new ItemManager();
    private static final WerewolfConfig config = new WerewolfConfig();
    private static final SkeletonManager skeletonManager = new SkeletonManager();
    private static final ShopManager shopManager = new ShopManager();
    private static final ScoreManager scoreManager = new ScoreManager();
    private static final TeamsManager teamsManager = new TeamsManager();
    private static MapManager mapManager;
    private static String statusMessage;
    private final UUID matchId;
    private final List<Player> participants;
    private final Map<UUID, WerewolfPlayerData> dataMap;
    private final Ticker ticker;
    private final RolePool pool;
    private final Tracker tracker = new Tracker();
    private WerewolfMap map;
    private boolean isPlaying;
    private boolean isNight;
    private boolean isWerewolfNight;
    private Timestamp startTime;
    private Timestamp endTime;
    private final List<ArmorStand> displayNameArmorStands;

    public WerewolfGame() {
        this.matchId = UUID.randomUUID();
        this.participants = new ArrayList<>();
        this.dataMap = new HashMap<>();
        this.pool = new RolePool(1,0,0,0);
        this.ticker = new Ticker();
        this.isPlaying = false;
        this.isNight = false;
        this.isWerewolfNight = false;
        this.displayNameArmorStands = new ArrayList<>();

        instance = this;
    }

    public WerewolfGame(WerewolfGame previousGame) {
        this.matchId = UUID.randomUUID();
        previousGame.participants.removeIf(p -> !Bukkit.getOnlinePlayers().contains(p));
        this.participants = previousGame.participants;
        this.dataMap = new HashMap<>();
        this.pool = previousGame.pool;
        this.ticker = new Ticker();
        this.map = previousGame.map;
        this.isPlaying = false;
        this.isNight = false;
        this.isWerewolfNight = false;
        this.displayNameArmorStands = new ArrayList<>();

        instance = this;
    }

    public static WerewolfGame getInstance() {
        return instance;
    }

    public static WerewolfConfig getConfig() {
        return config;
    }

    public static SkeletonManager getSkeletonManager() {
        return skeletonManager;
    }

    public static ShopManager getShopManager() {
        return shopManager;
    }

    public static ScoreManager getScoreManager() {
        return scoreManager;
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
        WerewolfPlugin.logConsole("Initializing worlds and maps");
        mapManager = new MapManager(new WorldManager());
    }

    public UUID getMatchId() {
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

    public boolean isWerewolfNight() {
        return isWerewolfNight;
    }

    public void setWerewolfNight(boolean werewolfNight) {
        isWerewolfNight = werewolfNight;
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
        } else if (instance.map == null || instance.map.equals(mapManager.getMapFromName("werewolf-maps" + File.separator + "lobby"))) {
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
        Bukkit.getPluginManager().registerEvents(listener, WerewolfPlugin.getPlugin());
        Bukkit.getPluginManager().registerEvents(skeletonManager, WerewolfPlugin.getPlugin());

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
                player.getInventory().clear();
                player.teleport(instance.map.getMapSpawn());


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
                    player.sendTitle(role.getBelievedRole().getColor() + role.getBelievedRole().getName(), ChatColor.GOLD + "GAME START", 2, 2, 40);
                    player.sendMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + ChatColor.RESET + "You are a " + role.getBelievedRole().getColor() + role.getBelievedRole().getName());

                    //prepare player
                    player.setGameMode(GameMode.ADVENTURE);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, 1000000, 4, false, false));
                    player.setHealth(40d);
                    player.setFoodLevel(20);
                    player.setSaturation(20);
                    player.getInventory().addItem(PlayerItem.SKELETON_PUNISHER.getItem());
                    player.getInventory().addItem(PlayerItem.EXQUISITE_MEAT.getItem());
                    player.getInventory().addItem(PlayerItem.MUTER.getItem());
                    if (config.getEnableServant().get() && role == Role.VAMPIRE) player.getInventory().addItem(PlayerItem.VAMPIRE_FANG.getItem());

                    //increment
                    count++;
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            }

        }

        Set<String> werewolves = Role.WEREWOLF.getTeam().getEntries();
        String werewolfPlayers = String.join(", ", werewolves);
        for (Player player : werewolves.stream().map(Bukkit::getPlayerExact).toList()) {
            player.sendMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + ChatColor.RESET +
                    "The werewolves are " + Role.WEREWOLF.getColor() + werewolfPlayers);
        }

        instance.ticker.start();
        instance.startTime = new Timestamp(System.currentTimeMillis());
    }

    private static void stop(Faction faction) {
        //stop ticker
        instance.ticker.stop();
        instance.endTime = new Timestamp(System.currentTimeMillis());

        //prepare and send stats
        instance.tracker.setResults(faction);
        instance.tracker.sendDataToDatabase(instance, faction);
        instance.tracker.logMatchResult(instance, faction);

        //clear skulls
        instance.map.getSkullLocations().forEach(v -> WerewolfUtil.resetSkull(instance.map.getWorld(), v));
        instance.displayNameArmorStands.forEach(ArmorStand::remove);

        //get role list (before resetting)
        String rolesList = getMatchText();

        //keep score gains of players
        Map<UUID, Integer> scoreGains = instance.tracker.getGains();

        //reset all values
        instance = new WerewolfGame(instance);
        World world = instance.map.getWorld();
        world.setTime(6000L);
        world.getWorldBorder().setCenter(world.getSpawnLocation());
        world.getWorldBorder().setSize(1000000d);
        teamsManager.clear();
        HandlerList.unregisterAll(listener);
        HandlerList.unregisterAll(skeletonManager);
        WerewolfTrap.clearTasks();

        //loop on players and remove other entities
        for (Entity entity : instance.map.getWorld().getEntities()) {
            if (entity.getType().equals(EntityType.SKELETON) || entity.getType().equals(EntityType.DROPPED_ITEM) || entity.getType().equals(EntityType.WITHER_SKELETON)) {
                entity.remove();
            }
            else if (entity instanceof Player player) {
                WerewolfUtil.playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE);
                player.sendMessage(rolesList);
                player.getInventory().clear();
                player.sendTitle(getEndString(faction), ChatColor.GOLD + "GAME END", 2, 2, 40);
                player.sendMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + getEndString(faction));
                player.setGameMode(GameMode.SPECTATOR);
                WerewolfUtil.removeAllPotionEffectsFromPlayer(player);
                player.setHealth(20d);
                player.setFoodLevel(20);
                player.setSaturation(20);

                Integer scoreGain = scoreGains.get(player.getUniqueId());
                if (scoreGain != null) WerewolfUtil.sendPluginText(player, "You gained " + ChatColor.LIGHT_PURPLE + scoreGain + ChatColor.AQUA + " points");
                scoreManager.assignPrefixSuffix(player);

                WerewolfBot bot = WerewolfBot.getBot();
                if (bot != null && bot.isConfigured()) {
                    long dcId = WerewolfBackend.getBackend().getPdc().getDiscordIdOfPlayer(player.getUniqueId()).join();
                    Optional<Member> dcMember = bot.getVc().getMembers().stream().filter(member -> member.getIdLong() == dcId).findAny();
                    dcMember.ifPresent(member -> member.mute(false).submit().thenAccept(r -> WerewolfUtil.sendPluginText(player, "You have been unmuted", ChatColor.GREEN)));
                    scoreManager.assignRole(player, bot.getGuild());
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
        if (teamsManager.getFactionSize(Faction.OUTSIDER) != 0) {
            stop(Faction.OUTSIDER);
        }
        else if (teamsManager.getFactionSize(Faction.VILLAGER) == 0) {
            stop(Faction.WEREWOLF);
        }
        else {
            stop(Faction.VILLAGER);
        }
    }

    private static String getEndString(Faction faction) {
        if (faction == null) return ChatColor.YELLOW + "Game Cancelled";
        return faction.getColor() + faction.getName() + " Victory!";
    }

    private static String getMatchText() {
        StringBuilder builder = new StringBuilder(ChatColor.AQUA + "======Werewolf Match Role======" + "\n");
        getTeamsManager().getData().forEach((faction, playerDataList) -> {
            if (!playerDataList.isEmpty()) {
                builder.append(faction.getColor())
                        .append(faction.getName())
                        .append("\n");
                playerDataList.forEach(playerData -> builder.append(ChatColor.RESET)
                        .append("> ")
                        .append(ChatColor.GOLD)
                        .append(playerData.getName())
                        .append(" (")
                        .append(playerData.getRole().getColor())
                        .append(playerData.getRole().getName())
                        .append(ChatColor.GOLD)
                        .append(")")
                        .append("\n"));
            }
        });
        builder.append(ChatColor.AQUA).append("=============================");
        return builder.toString();
    }
}
