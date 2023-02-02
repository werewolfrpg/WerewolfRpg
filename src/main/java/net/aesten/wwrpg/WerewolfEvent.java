package net.aesten.wwrpg;

import net.aesten.wwrpg.engine.WerewolfGame;
import com.aesten.wwrpg.engine.WerewolfPlayer;
import net.aesten.wwrpg.engine.WerewolfPlayerData;
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

        if (!WerewolfGame.getInstance().isPlaying()) {
            event.setJoinMessage(wwrpg.COLOR + "[Server]: " + ChatColor.AQUA + player.getName() + ChatColor.GOLD + " joined the server!");
        }

        WerewolfPlayerData.getDataMap().put(player.getUniqueId(), new WerewolfPlayerData());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (WerewolfGame.getInstance().isPlaying() && WerewolfGame.getInstance().isParticipant(player)) {
            event.setQuitMessage(null);
            //todo player stats (for database) will be saved and status disconnect instead of dead/alive
            WerewolfGame.getInstance().getParticipants().removeIf(wp -> wp.getId() == player.getUniqueId());
        }
        else {
            event.setQuitMessage(wwrpg.COLOR + wwrpg.LOG + ChatColor.AQUA + player.getName() + ChatColor.GOLD + " left the server!");
        }

        WerewolfPlayerData.getDataMap().remove(player.getUniqueId());
    }
}
