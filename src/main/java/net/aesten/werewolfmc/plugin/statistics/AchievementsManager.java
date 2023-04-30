package net.aesten.werewolfmc.plugin.statistics;

import net.azalealibrary.achievement.AzaleaAchievement;
import net.azalealibrary.achievement.achievement.Achievement;
import net.azalealibrary.achievement.achievement.AchievementCreator;
import net.azalealibrary.achievement.achievement.Frame;

import java.util.List;

public class AchievementsManager {
    public static void init() {
        List<Achievement> achievement = List.of(root, firstGame);
        AzaleaAchievement.Api.registerAchievements("WerewolfMC", achievement);
    }

    private static final Achievement root = AchievementCreator.begin()
            .setTickTrigger()
            .setIcon("minecraft:stone_axe")
            .setTitle("Werewolf mc achievement")
            .setDescription("Thank you for playing Werewolf mc!")
            .setFrame(Frame.TASK)
            .create("root");

    private static final Achievement firstGame = AchievementCreator.begin()
            .setIcon("minecraft:stick")
            .setTitle("First Game")
            .setDescription("Play a game for the first time")
            .setFrame(Frame.TASK)
            .setParent(root)
            .create("first_game");


}
