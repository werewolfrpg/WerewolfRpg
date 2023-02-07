package net.aesten.wwrpg.core;

import net.aesten.wwrpg.WerewolfRpg;
import net.aesten.wwrpg.utilities.WerewolfUtil;
import net.aesten.wwrpg.items.ItemRegistry;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Ticker {
    private final BossBar bar;
    private BukkitTask task;
    private int days;

    public Ticker() {
        this.bar = Bukkit.createBossBar("§eDay Time", BarColor.YELLOW, BarStyle.SEGMENTED_6);
        this.bar.setVisible(true);
        this.days = 0;
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }

    public void stop() {
        task.cancel();
    }


    public void start() {
        task = new BukkitRunnable() {
            int count = -1;
            double progress = 0.5;
            final double time = 1.0 / 240;
            final WerewolfGame game = WerewolfGame.getInstance();

            @Override
            public void run() {
                bar.setProgress(progress);
                progress = progress - time;
                tick(game);
                if (progress <= 0) {
                    if (WerewolfGame.getInstance().isNight())
                        switchToDay(game);
                    else
                        switchToNight(game);
                    count++;
                    progress = 1.0;
                }
            }
        }.runTaskTimer(WerewolfRpg.getPlugin(), 0, 10);
    }

    private void switchToNight(WerewolfGame game) {
        days++;
        bar.setColor(BarColor.PURPLE);
        bar.setTitle(ChatColor.DARK_PURPLE + "Night Time");
        game.getMap().getWorld().setTime(18000L);
        game.switchDayNight();

        //todo summon skeletons

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (game.getParticipants().contains(player)) {
                WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
                if (data.getRole() == Role.VAMPIRE && !data.isDead()) {
                    player.addPotionEffect(
                            new PotionEffect(
                                    PotionEffectType.DAMAGE_RESISTANCE,
                                    2400, 5,false, false, false));
                }
            }
            WerewolfUtil.sendTitle(player, ChatColor.DARK_PURPLE + "NIGHT TIME", ChatColor.GOLD + "Night " + days);
        }
    }

    private void switchToDay(WerewolfGame game) {
        bar.setColor(BarColor.YELLOW);
        bar.setTitle(ChatColor.YELLOW + "Day Time");
        game.getMap().getWorld().setTime(6000L);
        game.switchDayNight();

        for (Entity entity : game.getMap().getWorld().getEntities()) {
            if (entity.getType() == EntityType.SKELETON) {
                entity.remove();
            }
            else if (entity instanceof Player player) {
                if (game.getParticipants().contains(player)) {
                    WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
                    player.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                    data.resetTemporaryValues();
                }
                WerewolfUtil.sendTitle(player, ChatColor.YELLOW + "DAY TIME", ChatColor.GOLD + "Day " + days);
            }
        }
    }

    private void tick(WerewolfGame game) {
        if ((game.getFaction(Role.VILLAGER).size() == 0 && game.getFaction(Role.POSSESSED).size() == 0) ||
                game.getFaction(Role.WEREWOLF).size() == 0) {
            WerewolfGame.endGame();
        }
        else {
            for (Player player : game.getParticipants()) {
                Inventory inventory = player.getInventory();
                WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());

                if (inventory.contains(ItemRegistry.DIVINATION.getItemStack().getType())) {
                    int div = 0;
                    for (int i = 0 ; i < inventory.getSize() ; i++) {
                        ItemStack is = inventory.getItem(i);
                        if (is != null && is.getType() == Material.HEART_OF_THE_SEA) {
                            div += is.getAmount();
                        }
                    }
                    inventory.remove(ItemRegistry.DIVINATION.getItemStack().getType());
                    data.setRemainingDivinations(data.getRemainingDivinations() + div);
                }

                if (data.getRemainingDivinations() != 0) {
                    if (!data.hasAlreadyUsedDivination()) {
                        BaseComponent[] component =
                                new ComponentBuilder("Remaining Divinations: " + data.getRemainingDivinations())
                                        .color(net.md_5.bungee.api.ChatColor.BLUE).create();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(component));
                    }
                    else {
                        BaseComponent[] component =
                                new ComponentBuilder("Remaining Divinations: " + data.getRemainingDivinations())
                                        .color(net.md_5.bungee.api.ChatColor.GRAY).strikethrough(true).create();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(component));
                    }
                }

                if (inventory.contains(ItemRegistry.WEREWOLF_AXE.getItemStack()) && data.getRole() != Role.WEREWOLF) {
                    inventory.remove(ItemRegistry.WEREWOLF_AXE.getItemStack());
                    WerewolfUtil.sendPluginText(player, "Werewolf Axe deleted from inventory", ChatColor.RED);
                    WerewolfUtil.sendPluginText(player, "You cannot use this item", ChatColor.RED);
                }

                if (inventory.contains(ItemRegistry.TRAITORS_GUIDE.getItemStack()) && data.getRole() != Role.TRAITOR) {
                    inventory.remove(ItemRegistry.TRAITORS_GUIDE.getItemStack());
                    WerewolfUtil.sendPluginText(player, "Traitor's Guide deleted from inventory", ChatColor.RED);
                    WerewolfUtil.sendPluginText(player, "You cannot use this item", ChatColor.RED);
                }

                if (data.hasActiveSneakNotice() && data.hasBeenDivinated()) {
                    WerewolfUtil.sendPluginText(player, "(Sneak Notice) Your identity has been unveiled", ChatColor.DARK_RED);
                    data.setHasBeenDivinated(false);
                }
            }
        }
    }
}
