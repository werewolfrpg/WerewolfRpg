package net.aesten.werewolfrpg.items.registry.player;

import net.aesten.werewolfbot.WerewolfBot;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.data.WerewolfPlayerData;
import net.aesten.werewolfrpg.items.base.InteractItem;
import net.aesten.werewolfrpg.items.base.ItemStackBuilder;
import net.aesten.werewolfrpg.items.base.WerewolfItem;
import net.aesten.werewolfrpg.utilities.WerewolfUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;


public class Muter extends WerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "muter";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.ALLIUM, 1)
                .addName(ChatColor.GOLD + "Muter")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Switch mute/unmute on discord")
                .addLore(ChatColor.GRAY + "The bot has to be active for the item to activate")
                .addLore(ChatColor.GRAY + "You need to be in the session's discord")
                .addLore(ChatColor.GRAY + "You cannot unmute yourself during night time")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        WerewolfGame game = WerewolfGame.getInstance();
        WerewolfBot bot = WerewolfRpg.getBot();
        Player user = event.getPlayer();

        if (bot == null) {
            WerewolfUtil.sendPluginText(user, "Bot is not enabled", ChatColor.RED);
            return;
        }

        if (bot.getCurrentSession() == null) {
            WerewolfUtil.sendPluginText(user, "There is no active session", ChatColor.RED);
            return;
        }

        if (game.isNight()) {
            WerewolfUtil.sendPluginText(user, "You cannot use this item during night time", ChatColor.RED);
            return;
        }

        List<String> str = QueryManager.getDiscordIdsOfPlayer(user.getUniqueId().toString());
        VoiceChannel vc = bot.getCurrentSession().getVc();
        List<Member> dcMember = vc.getMembers().stream().filter(member -> str.contains(member.getId())).toList();

        if (dcMember.isEmpty()) {
            WerewolfUtil.sendPluginText(user, "Could not find your discord account in vc", ChatColor.RED);
            return;
        }

        WerewolfPlayerData data = game.getDataMap().get(user.getUniqueId());

        if (data.isForceMute()) {
            data.setForceMute(false);
            dcMember.forEach(member -> member.mute(false).queue());
            WerewolfUtil.sendPluginText(user, "You are now unmute", ChatColor.GREEN);
        } else {
            data.setForceMute(true);
            dcMember.forEach(member -> member.mute(true).queue());
            WerewolfUtil.sendPluginText(user, "You are now muted", ChatColor.GREEN);
        }
    }
}