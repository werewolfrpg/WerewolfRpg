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
import net.aesten.werewolfmc.plugin.items.registry.PlayerItem;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import net.aesten.werewolfmc.backend.models.PlayerStats;
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
                PlayerStats stats = WerewolfGame.getInstance().getTracker().getPlayerStats(player.getUniqueId());
                stats.setDeathCause("Disconnection");
                stats.setKiller(player.getUniqueId());
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

    //prevent item drops before outside match & skeleton slicer drop
    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.getPlayer().isOp()) return;
        if (!WerewolfGame.getInstance().isPlaying() || WerewolfUtil.sameItem(event.getItemDrop().getItemStack(), PlayerItem.SKELETON_SLICER.getItem())) event.setCancelled(true);
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
            WerewolfGame game = WerewolfGame.getInstance();
             if (game.isPlaying() && game.isParticipant(player)) {
                 UUID id = player.getUniqueId();
                 game.getDataMap().get(id).setAlive(false);
                 WerewolfGame.getTeamsManager().playerDied(player);
                 player.setGameMode(GameMode.SPECTATOR);
                 player.getInventory().clear();
                 player.getActivePotionEffects().clear();

                 //handle vampire-servant
                 if (game.getDataMap().get(id).getRole() == Role.VAMPIRE) {
                     long vampiresLeft = game.getParticipants().stream().filter(p -> {
                         WerewolfPlayerData data = game.getDataMap().get(p.getUniqueId());
                         return data.getRole() == Role.VAMPIRE && data.isAlive();
                     }).count();
                     if (vampiresLeft == 0) {
                         for (Player participant : game.getParticipants()) {
                             if (game.getDataMap().get(participant.getUniqueId()).getRole() == Role.SERVANT) {
                                 participant.setHealth(0);
                                 PlayerStats stats = game.getTracker().getPlayerStats(participant.getUniqueId());
                                 stats.setKiller(participant.getUniqueId());
                                 stats.setDeathCause("Vampire Death");
                             }
                         }
                     }
                 }

                 //stats
                 AbstractMap.SimpleEntry<String, UUID> deathData = game.getTracker().getSpecificDeathCauses().get(id);
                 PlayerStats stats = game.getTracker().getPlayerStats(id);
                 String deathCause;
                 if (deathData == null) {
                     deathCause = "Unfortunate Accident";
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
            event.setRespawnLocation(event.getPlayer().getWorld().getSpawnLocation());
        } else {
            event.setRespawnLocation(map.getMapSpawn());
        }
    }
}
