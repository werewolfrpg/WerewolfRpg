package net.aesten.werewolfrpg.statistics;

import net.azalealibrary.achievements.AzaleaAchievements;
import net.azalealibrary.achievements.achievement.Achievement;
import net.azalealibrary.achievements.achievement.AchievementCreator;
import net.azalealibrary.achievements.achievement.Frame;

import java.util.List;

public class AchievementsManager {
    public static void init() {
        List<Achievement> achievements = List.of(root, firstGame);
        AzaleaAchievements.Api.registerAchievements("WerewolfRPG", achievements);
    }

    private static final Achievement root = AchievementCreator.begin()
            .setTickTrigger()
            .setIcon("minecraft:stone_axe")
            .setTitle("Werewolf RPG Achievements")
            .setDescription("Thank you for playing Werewolf RPG!")
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
