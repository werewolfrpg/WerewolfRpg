package net.aesten.werewolfmc.plugin.events;

import net.aesten.werewolfmc.WerewolfPlugin;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import net.aesten.werewolfmc.plugin.items.base.EntityInteractItem;
import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.items.registry.AdminItem;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.statistics.Result;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class GeneralEvents implements Listener {
    //handle player connection
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //check if user is registered
        WerewolfBot bot = WerewolfBot.getBot();
        if (bot == null) return;

        List<UUID> mcIds = WerewolfBackend.getBackend().getPdc().getAllMinecraftIds().join();

        if (!mcIds.contains(player.getUniqueId())) {
            event.getPlayer().kickPlayer("[Werewolf]\nYou are not registered on a valid Discord server!\nYour Minecraft username to register is: " + player.getName());
            event.setJoinMessage(null);
        } else {
            WerewolfGame.getScoreManager().assignPrefixSuffix(player);
            player.setPlayerListHeader(ChatColor.GOLD + "Werewolf Minigame Server");

            if (WerewolfGame.getInstance().isPlaying()) {
                if (!WerewolfGame.getInstance().isParticipant(player)) {
                    WerewolfGame.getTeamsManager().registerPlayerRole(player, Role.SPECTATOR);
                }
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            } else {
                if (WerewolfGame.getInstance().getParticipants().stream().map(Player::getUniqueId).toList().contains(player.getUniqueId())) {
                    WerewolfGame.getInstance().getParticipants().removeIf(p -> p.getUniqueId().equals(player.getUniqueId()));
                    WerewolfGame.getInstance().getParticipants().add(player);
                }
                event.setJoinMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG +
                        ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.AQUA + " joined the server!");
            }
        }
    }

    //handle player disconnect
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (WerewolfGame.getInstance().isPlaying() && WerewolfGame.getInstance().isParticipant(player)) {
            WerewolfPlayerData data = WerewolfGame.getInstance().getDataMap().get(player.getUniqueId());
            if (data.isAlive()) {
                event.setQuitMessage(null);
                WerewolfGame.getTeamsManager().playerDied(player);
                WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).setAlive(false);
                WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId()).setResult(Result.DISCONNECTED);
            }
        }
        else {
            event.setQuitMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.AQUA + " left the server!");
        }
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        event.setLeaveMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + ChatColor.RED + event.getPlayer().getName() + ChatColor.AQUA + " tried to join server!");
    }

    //split chat between spectators and players when in-game
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        if (WerewolfGame.getInstance().isPlaying() && event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            for (Player receiver : event.getRecipients()) {
                if (receiver.getGameMode() == GameMode.SPECTATOR) {
                    receiver.sendMessage(ChatColor.LIGHT_PURPLE + "<" + event.getPlayer().getName() + "> " + ChatColor.RESET + event.getMessage());
                }
            }
        }
        else if (!WerewolfGame.getInstance().isNight()) {
            for (Player receiver : event.getRecipients()) {
                receiver.sendMessage(ChatColor.GREEN + "<" + event.getPlayer().getName() + "> " + ChatColor.RESET + event.getMessage());
            }
        }
    }

    //regulate in-game messaging
    @EventHandler
    public void onPM(PlayerCommandPreprocessEvent event) {
        if (WerewolfGame.getInstance().isPlaying()) {
            String message = event.getMessage();
            if (message.contains("/msg") || message.contains("/tell") || message.contains("/tellraw") || message.contains("/teammsg")) {
                event.getPlayer().sendMessage(WerewolfPlugin.COLOR + WerewolfPlugin.CHAT_LOG + ChatColor.RED + "You cannot use this command in-game!");
                event.setCancelled(true);
            }
        }
    }

    //prevent player interaction with map decorations
    @EventHandler
    public void onRightClickBlock(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && !event.getPlayer().isOp()) {
            if (event.getClickedBlock() != null) {
                switch (event.getClickedBlock().getType()) {
                    case BARREL, LEVER, CAKE, CHEST, TRAPPED_CHEST ->
                            event.setCancelled(true);
                }
            }
        }
    }

    //admin item events
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND || event.getItem() == null) return;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Optional<WerewolfItem> werewolfItem = AdminItem.getRegistry().values().stream().filter(item -> WerewolfUtil.sameItem(item.getItem(), event.getItem())).findAny();
            if (werewolfItem.isPresent() && werewolfItem.get() instanceof InteractItem interactItem) {
                interactItem.onPlayerInteract(event);
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent event) {
        ItemStack clickItem = event.getPlayer().getInventory().getItemInMainHand();
        if (clickItem.getItemMeta() == null) return;
        Optional<WerewolfItem> werewolfItem = AdminItem.getRegistry().values().stream().filter(item -> WerewolfUtil.sameItem(item.getItem(), clickItem)).findAny();
        if (werewolfItem.isPresent() && werewolfItem.get() instanceof EntityInteractItem entityInteractItem) {
            entityInteractItem.onEntityInteract(event);
        }
    }


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
             if (WerewolfGame.getInstance().isPlaying() && WerewolfGame.getInstance().isParticipant(player)) {
                 UUID id = player.getUniqueId();
                 WerewolfGame.getInstance().getDataMap().get(id).setAlive(false);
                 WerewolfGame.getTeamsManager().playerDied(player);
                 player.setGameMode(GameMode.SPECTATOR);
                 player.getInventory().clear();
                 player.getActivePotionEffects().clear();

                 //stats
                 AbstractMap.SimpleEntry<String, UUID> deathData = WerewolfGame.getInstance().getTracker().getSpecificDeathCauses().get(id);
                 PlayerStats stats = WerewolfGame.getInstance().getTracker().getPlayerStats(id);
                 String deathCause;
                 if (deathData == null) {
                     deathCause = Objects.requireNonNull(event.getEntity().getLastDamageCause()).getCause().name();
                 } else {
                     deathCause = deathData.getKey();
                     stats.setKiller(deathData.getValue());
                 }
                 stats.setDeathCause(deathCause);
             }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        WerewolfMap map = WerewolfGame.getInstance().getMap();
        if (map == null) {
            event.setRespawnLocation(WerewolfGame.getMapManager().getMapFromName("lobby").getMapSpawn());
        } else {
            event.setRespawnLocation(map.getMapSpawn());
        }
    }
}