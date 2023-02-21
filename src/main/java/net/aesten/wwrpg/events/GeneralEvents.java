package net.aesten.wwrpg.events;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.core.WerewolfGame;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;

public class GeneralEvents implements Listener {
    //change join message
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG +
                ChatColor.GREEN + event.getPlayer().getName() + ChatColor.GOLD + " joined the server!");
    }

    //handle player disconnect
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (WerewolfGame.getInstance().isPlaying() && WerewolfGame.getInstance().isParticipant(player)) {
            event.setQuitMessage(null);
            //todo player stats (for database) will be saved and status disconnect instead of dead/alive
            WerewolfGame.getInstance().removeFromFaction(player);
            WerewolfGame.getInstance().getDataMap().remove(player.getUniqueId());
        }
        else {
            event.setQuitMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + ChatColor.GREEN + player.getName() + ChatColor.GOLD + " left the server!");
        }
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
        else {
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
                event.getPlayer().sendMessage(WerewolfRpg.COLOR + WerewolfRpg.CHAT_LOG + ChatColor.RED + "You cannot use this command in-game!");
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

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (WerewolfGame.getInstance().isPlaying()) {
            LivingEntity entity = event.getEntity();
            if (event.getEntity().getType() == EntityType.PLAYER) {
                Player player = (Player) entity;
                WerewolfGame.getInstance().getDataMap().get(player.getUniqueId()).setAlive(false);
                WerewolfGame.getInstance().removeFromFaction(player);
                player.setGameMode(GameMode.SPECTATOR);
                player.getInventory().clear();
                player.getActivePotionEffects().clear();
            }
        }
    }
}
