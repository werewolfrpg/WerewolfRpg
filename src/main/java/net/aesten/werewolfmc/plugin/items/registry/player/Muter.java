package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
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
        WerewolfBot bot = WerewolfBot.getBot();
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

        user.setCooldown(Material.ALLIUM, cooldown*20);
        cooldowns.put(user, Instant.now());

        long dcId = WerewolfBackend.getBackend().getPdc().getDiscordIdOfPlayer(user.getUniqueId()).join();
        VoiceChannel vc = bot.getVc();
        Optional<Member> dcMemberOpt = vc.getMembers().stream().filter(member -> member.getIdLong() == dcId).findAny();

        if (dcMemberOpt.isEmpty()) {
            WerewolfUtil.sendPluginText(user, "Could not find your discord account in vc", ChatColor.RED);
            return;
        }

        Member dcMember = dcMemberOpt.get();
        WerewolfPlayerData data = game.getDataMap().get(user.getUniqueId());

        if (data.isForceMute()) {
            data.setForceMute(false);
            dcMember.mute(false).submit().thenAccept(r -> WerewolfUtil.sendPluginText(user, "You are now unmuted", ChatColor.GREEN));
        } else {
            data.setForceMute(true);
            dcMember.mute(true).submit().thenAccept(r -> WerewolfUtil.sendPluginText(user, "You are now muted", ChatColor.GREEN));
        }


    }
}
