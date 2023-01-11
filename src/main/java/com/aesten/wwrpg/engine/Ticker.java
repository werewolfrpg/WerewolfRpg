package com.aesten.wwrpg.engine;

import com.aesten.wwrpg.wwrpg;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Ticker {
    private final BossBar bar;
    private BukkitTask task;
    private int days;
    private Runnable runAtProgressEnd;

    public Ticker() {
        this.bar = Bukkit.createBossBar("§eDay Time", BarColor.YELLOW, BarStyle.SEGMENTED_6);
        this.bar.setVisible(true);
        this.days = 0;
    }

    public void setDayBar() {
        bar.setColor(BarColor.YELLOW);
        bar.setTitle("§eDay Time");
    }

    public void setNightBar() {
        bar.setColor(BarColor.PURPLE);
        bar.setTitle("§5Night Time");
    }

    public void addPlayer(Player player) {
        bar.addPlayer(player);
    }
    public void removePlayer(Player player) {
        bar.removePlayer(player);
    }

    public void stop() {
        task.cancel();
    }

    public void incrementDays() {
        days++;
    }

    public int getDays() {
        return days;
    }

    public void cast() {
        task = new BukkitRunnable() {
            int count = -1;
            double progress = 0.5;
            final double time = 1.0 / 120;

            @Override
            public void run() {
                bar.setProgress(progress);

                progress = progress - time;
                if (progress <= 0) {
                    if (isday) {
                        bar.setColor(BarColor.PURPLE);
                        bar.setTitle("§5Night Time");
                        switchNight();
                    }
                    if (count == 0) {
                        bar.setColor(BarColor.YELLOW);
                        bar.setTitle("§eDay Time");
                        switchDay();
                    }
                    count++;
                    progress = 1.0;
                }
            }
        }.runTaskTimer(wwrpg.getPlugin(), 0, 20);
    }

    private static void switchNight() {

    }

    private static void switchDay() {

    }

}
