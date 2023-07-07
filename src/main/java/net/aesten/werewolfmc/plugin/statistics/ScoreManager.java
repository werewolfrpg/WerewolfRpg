package net.aesten.werewolfmc.plugin.statistics;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.PlayerStats;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;
import net.azalealibrary.configuration.property.Property;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

public class ScoreManager {
    private final LinkedHashMap<Rank, Property<Integer>> rankThresholdMap = new LinkedHashMap<>();

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

    public int getScoreThresholdOfRank(Rank rank) {
        return rankThresholdMap.get(rank).get();
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

    public Rank getNextKey(Rank key) {
        List<Rank> keys = new ArrayList<>(rankThresholdMap.keySet());
        int index = keys.indexOf(key);
        if (index < 0 || index >= keys.size() - 1) return null;
        return keys.get(index + 1);
    }

    public int getCalculatedScore(PlayerStats stats) {
        int score = 0;
        if (stats.getResult() == Result.VICTORY) {
            if (stats.getRole().getFaction() == Faction.OUTSIDER) score += WerewolfGame.getConfig().getThirdPartyVictoryScoreGain().get();
            else if (stats.getRole() == Role.TRAITOR) score += WerewolfGame.getConfig().getTraitorVictoryScoreGain().get();
            else score += WerewolfGame.getConfig().getBaseVictoryScoreGain().get();
        } else if (stats.getResult() == Result.DEFEAT) {
            if (stats.getRole().getFaction() == Faction.OUTSIDER) score += WerewolfGame.getConfig().getThirdPartyDefeatScoreGain().get();
            else score += WerewolfGame.getConfig().getBaseDefeatScoreGain().get();
        }

        score += stats.getByItemName("Traitor's Guide").getStats().get("used") * 2;
        score += stats.getByItemName("Divination").getStats().get("used") * 2;
        score += stats.getByItemName("Ash of the Dead").getStats().get("used");
        score += stats.getByItemName("Revelation").getStats().get("used");
        score += stats.getByItemName("Invisibility Potion").getStats().get("used");
        score += stats.getByItemName("Protection").getStats().get("triggered") * 3;
        score += stats.getByItemName("Sneak Notice").getStats().get("triggered") * 3;
        score += stats.getByItemName("Stun Grenade").getStats().get("hitTargets") * 2;
        score += stats.getByItemName("Curse Spear (Melee)").getStats().get("cursed") * 3;
        score += stats.getByItemName("Curse Spear (Thrown)").getStats().get("cursed") * 4;
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

        List<net.dv8tion.jda.api.entities.Role> newRoles = guild.getRolesByName(rank.getName(), true);
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
        List<net.dv8tion.jda.api.entities.Role> beginner = guild.getRolesByName(Rank.BEGINNER.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> novice = guild.getRolesByName(Rank.NOVICE.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> apprentice = guild.getRolesByName(Rank.APPRENTICE.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> intermediate = guild.getRolesByName(Rank.INTERMEDIATE.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> skilled = guild.getRolesByName(Rank.SKILLED.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> experienced = guild.getRolesByName(Rank.EXPERIENCED.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> veteran = guild.getRolesByName(Rank.VETERAN.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> expert = guild.getRolesByName(Rank.EXPERT.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> elite = guild.getRolesByName(Rank.ELITE.getName(), true);
        List<net.dv8tion.jda.api.entities.Role> legendary = guild.getRolesByName(Rank.LEGENDARY.getName(), true);

        if (beginner.size() != 0 && novice.size() != 0 && apprentice.size() != 0 && intermediate.size() != 0 && skilled.size() != 0 &&
                experienced.size() != 0 && veteran.size() != 0 && expert.size() != 0 && elite.size() != 0 && legendary.size() != 0) {
            roles.addAll(List.of(beginner.get(0), novice.get(0), apprentice.get(0), intermediate.get(0), skilled.get(0),
                    experienced.get(0), veteran.get(0), expert.get(0), elite.get(0), legendary.get(0)));
        }
    }
}
