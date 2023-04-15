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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class Muter extends WerewolfItem implements InteractItem {
    private final Map<Player, Instant> cooldowns = new HashMap<>();

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
                .addLore(ChatColor.GRAY + "You need to be in the correct voice channel")
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

        if (!bot.isConfigured()) {
            WerewolfUtil.sendPluginText(user, "Bot is not configured properly", ChatColor.RED);
            return;
        }

        if (game.isNight()) {
            WerewolfUtil.sendPluginText(user, "You cannot use this item during night time", ChatColor.RED);
            return;
        }

        int cooldown = 5;

        if (cooldowns.get(user) != null) {
            if (ChronoUnit.SECONDS.between(cooldowns.get(user), Instant.now()) < cooldown) {
                WerewolfUtil.sendPluginText(user, "Wait for the end of the cooldown", ChatColor.RED);
                return;
            }
        }

        String dcId = QueryManager.getDiscordIdOfPlayer(user.getUniqueId().toString());
        VoiceChannel vc = bot.getVc();
        Optional<Member> dcMemberOpt = vc.getMembers().stream().filter(member -> member.getId().equals(dcId)).findAny();

        if (dcMemberOpt.isEmpty()) {
            WerewolfUtil.sendPluginText(user, "Could not find your discord account in vc", ChatColor.RED);
            return;
        }

        Member dcMember = dcMemberOpt.get();
        WerewolfPlayerData data = game.getDataMap().get(user.getUniqueId());

        if (data.isForceMute()) {
            data.setForceMute(false);
            dcMember.mute(false).queue();
            WerewolfUtil.sendPluginText(user, "You are now unmute", ChatColor.GREEN);
        } else {
            data.setForceMute(true);
            dcMember.mute(true).queue();
            WerewolfUtil.sendPluginText(user, "You are now muted", ChatColor.GREEN);
        }

        user.setCooldown(Material.ALLIUM, cooldown*20);
        cooldowns.put(user, Instant.now());
    }
}
