package net.aesten.werewolfmc.plugin.statistics;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.azalealibrary.configuration.property.Property;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreManager {
    private final Map<Rank, Property<Integer>> rankThresholdMap = new HashMap<>();

    private final List<net.dv8tion.jda.api.entities.Role> roles = new ArrayList<>();

    public ScoreManager() {
        rankThresholdMap.put(Rank.BEGINNER, WerewolfGame.getConfig().getRank0());
        rankThresholdMap.put(Rank.NOVICE, WerewolfGame.getConfig().getRank1());
        rankThresholdMap.put(Rank.APPRENTICE, WerewolfGame.getConfig().getRank2());
        rankThresholdMap.put(Rank.INTERMEDIATE, WerewolfGame.getConfig().getRank3());
        rankThresholdMap.put(Rank.SKILLED, WerewolfGame.getConfig().getRank4());
        rankThresholdMap.put(Rank.EXPERIENCED, WerewolfGame.getConfig().getRank5());
        rankThresholdMap.put(Rank.VETERAN, WerewolfGame.getConfig().getRank6());
        rankThresholdMap.put(Rank.EXPERT, WerewolfGame.getConfig().getRank7());
        rankThresholdMap.put(Rank.ELITE, WerewolfGame.getConfig().getRank8());
        rankThresholdMap.put(Rank.LEGENDARY, WerewolfGame.getConfig().getRank9());
    }

    public Rank getPlayerRank(Player player) {
        int score = WerewolfBackend.getBackend().getPdc().getScoreOfPlayer(player.getUniqueId()).join();
        return getScoreRank(score);
    }

    public Rank getScoreRank(int score) {
        Map.Entry<Rank, Property<Integer>> bestEntry = Map.entry(Rank.BEGINNER, WerewolfGame.getConfig().getRank0());
        for (Map.Entry<Rank, Property<Integer>> entry : rankThresholdMap.entrySet()) {
            if (score >= entry.getValue().get() && entry.getValue().get() >= bestEntry.getValue().get()) {
                bestEntry = entry;
            }
        }
        return bestEntry.getKey();
    }

    public int getCalculatedScore(PlayerStats stats) {
        int score = 0;
        if (stats.getResult() == Result.VICTORY) {
            if (stats.getRole() == Role.VAMPIRE) score += WerewolfGame.getConfig().getVampireVictoryScoreGain().get();
            else if (stats.getRole() == Role.TRAITOR) score += WerewolfGame.getConfig().getTraitorVictoryScoreGain().get();
            else score += WerewolfGame.getConfig().getBaseVictoryScoreGain().get();
        } else if (stats.getResult() == Result.DEFEAT) {
            if (stats.getRole() == Role.VAMPIRE) score += WerewolfGame.getConfig().getVampireDefeatScoreGain().get();
            else score += WerewolfGame.getConfig().getBaseDefeatScoreGain().get();
        }

        score += stats.getTraitorsGuideUsed() * 2;
        score += stats.getDivinationUsed() * 2;
        score += stats.getAshUsed();
        score += stats.getRevelationUsed();
        score += stats.getInvisibilityUsed();
        score += stats.getProtectionTriggered() * 3;
        score += stats.getSneakNoticeTriggered() * 3;
        score += stats.getStunGrenadeHitTargets() * 2;
        score += stats.getCurseSpearMeleeCurses() * 3;
        score += stats.getCurseSpearThrowCurses() * 4;
        score += stats.getKills() * 5;

        return score;
    }

    public void assignPrefixSuffix(Player player) {
        int score = WerewolfBackend.getBackend().getPdc().getScoreOfPlayer(player.getUniqueId()).join();
        assignPrefixSuffix(player, score);
    }

    public void assignPrefixSuffix(Player player, int score) {
        Rank rank = getScoreRank(score);
        player.setPlayerListName(rank.getPrefix() + player.getName() + ChatColor.GREEN + " ☆" + score);
    }

    public void assignRole(Player player, Guild guild) {
        assignRole(WerewolfBackend.getBackend().getPdc().getDiscordIdOfPlayer(player.getUniqueId()).join(), guild, WerewolfGame.getScoreManager().getPlayerRank(player));
    }

    public void assignRole(long dcId, Guild guild, Rank rank) {
        if (roles.size() == 0) getAllDiscordRoles(guild);

        List<net.dv8tion.jda.api.entities.Role> newRoles = guild.getRolesByName(rank.name(), true);
        if (newRoles.size() == 0) return;
        net.dv8tion.jda.api.entities.Role newRole = newRoles.get(0);

        Member member = guild.getMemberById(dcId);
        if (member != null) {
            if (!member.getRoles().contains(newRole)) {
                roles.forEach(role -> {
                    if (member.getRoles().contains(role)) {
                        guild.removeRoleFromMember(member, role).complete();
                    }
                });
                guild.addRoleToMember(member, newRole).complete();
            }
        }
    }

    public List<net.dv8tion.jda.api.entities.Role> getRankRoles(Guild guild) {
        if (roles.size() == 0) getAllDiscordRoles(guild);
        return roles;
    }

    private void getAllDiscordRoles(Guild guild) {
        List<net.dv8tion.jda.api.entities.Role> beginner = guild.getRolesByName(Rank.BEGINNER.name(), true);
        List<net.dv8tion.jda.api.entities.Role> novice = guild.getRolesByName(Rank.NOVICE.name(), true);
        List<net.dv8tion.jda.api.entities.Role> apprentice = guild.getRolesByName(Rank.APPRENTICE.name(), true);
        List<net.dv8tion.jda.api.entities.Role> intermediate = guild.getRolesByName(Rank.INTERMEDIATE.name(), true);
        List<net.dv8tion.jda.api.entities.Role> skilled = guild.getRolesByName(Rank.SKILLED.name(), true);
        List<net.dv8tion.jda.api.entities.Role> experienced = guild.getRolesByName(Rank.EXPERIENCED.name(), true);
        List<net.dv8tion.jda.api.entities.Role> veteran = guild.getRolesByName(Rank.VETERAN.name(), true);
        List<net.dv8tion.jda.api.entities.Role> expert = guild.getRolesByName(Rank.EXPERT.name(), true);
        List<net.dv8tion.jda.api.entities.Role> elite = guild.getRolesByName(Rank.ELITE.name(), true);
        List<net.dv8tion.jda.api.entities.Role> legendary = guild.getRolesByName(Rank.LEGENDARY.name(), true);

        if (beginner.size() != 0 && novice.size() != 0 && apprentice.size() != 0 && intermediate.size() != 0 && skilled.size() != 0 &&
                experienced.size() != 0 && veteran.size() != 0 && expert.size() != 0 && elite.size() != 0 && legendary.size() != 0) {
            roles.addAll(List.of(beginner.get(0), novice.get(0), apprentice.get(0), intermediate.get(0), skilled.get(0),
                    experienced.get(0), veteran.get(0), expert.get(0), elite.get(0), legendary.get(0)));
        }
    }
}
