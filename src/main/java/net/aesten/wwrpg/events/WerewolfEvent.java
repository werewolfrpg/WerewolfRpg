package net.aesten.wwrpg.events;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.core.WerewolfGame;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;

public class WerewolfEvent implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        event.setJoinMessage(WerewolfRpg.COLOR + WerewolfRpg.LOG + ChatColor.AQUA + player.getName() + ChatColor.GOLD + " joined the server!");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (WerewolfGame.getInstance().isPlaying() && WerewolfGame.getInstance().isParticipant(player)) {
            event.setQuitMessage(null);
            //todo player stats (for database) will be saved and status disconnect instead of dead/alive
            WerewolfGame.getInstance().getDataMap().remove(player.getUniqueId());
        }
        else {
            event.setQuitMessage(WerewolfRpg.COLOR + WerewolfRpg.LOG + ChatColor.AQUA + player.getName() + ChatColor.GOLD + " left the server!");
        }
    }
}
