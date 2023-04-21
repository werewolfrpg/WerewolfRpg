package net.aesten.werewolfrpg.plugin.statistics;

import net.aesten.werewolfrpg.backend.WerewolfBackend;
import net.aesten.werewolfrpg.backend.models.PlayerStats;
import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.aesten.werewolfrpg.plugin.data.Role;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.AssignmentPolicy;
import net.azalealibrary.configuration.property.ConfigurableProperty;
import net.azalealibrary.configuration.property.Property;
import net.azalealibrary.configuration.property.PropertyType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreManager implements Configurable {
    private static final AssignmentPolicy<Integer> POSITIVE = AssignmentPolicy.create(i -> i >= 0, "Score threshold should be positive");

    private final Property<Integer> rank0 = Property.create(PropertyType.INTEGER, "rank-score.beginner", () -> 0).addPolicy(POSITIVE).done();
    private final Property<Integer> rank1 = Property.create(PropertyType.INTEGER, "rank-score.novice", () -> 100).addPolicy(POSITIVE).done();
    private final Property<Integer> rank2 = Property.create(PropertyType.INTEGER, "rank-score.apprentice", () -> 200).addPolicy(POSITIVE).done();
    private final Property<Integer> rank3 = Property.create(PropertyType.INTEGER, "rank-score.intermediate", () -> 300).addPolicy(POSITIVE).done();
    private final Property<Integer> rank4 = Property.create(PropertyType.INTEGER, "rank-score.skilled", () -> 400).addPolicy(POSITIVE).done();
    private final Property<Integer> rank5 = Property.create(PropertyType.INTEGER, "rank-score.experienced", () -> 500).addPolicy(POSITIVE).done();
    private final Property<Integer> rank6 = Property.create(PropertyType.INTEGER, "rank-score.veteran", () -> 700).addPolicy(POSITIVE).done();
    private final Property<Integer> rank7 = Property.create(PropertyType.INTEGER, "rank-score.expert", () -> 1000).addPolicy(POSITIVE).done();
    private final Property<Integer> rank8 = Property.create(PropertyType.INTEGER, "rank-score.elite", () -> 1500).addPolicy(POSITIVE).done();
    private final Property<Integer> rank9 = Property.create(PropertyType.INTEGER, "rank-score.legendary", () -> 2000).addPolicy(POSITIVE).done();

    private final Map<Rank, Property<Integer>> rankThresholdMap = new HashMap<>();

    private final List<net.dv8tion.jda.api.entities.Role> roles = new ArrayList<>();

    public ScoreManager() {
        rankThresholdMap.put(Rank.BEGINNER, rank0);
        rankThresholdMap.put(Rank.NOVICE, rank1);
        rankThresholdMap.put(Rank.APPRENTICE, rank2);
        rankThresholdMap.put(Rank.INTERMEDIATE, rank3);
        rankThresholdMap.put(Rank.SKILLED, rank4);
        rankThresholdMap.put(Rank.EXPERIENCED, rank5);
        rankThresholdMap.put(Rank.VETERAN, rank6);
        rankThresholdMap.put(Rank.EXPERT, rank7);
        rankThresholdMap.put(Rank.ELITE, rank8);
        rankThresholdMap.put(Rank.LEGENDARY, rank9);
    }

    @Override
    public String getName() {
        return "wwrpg-score-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        return List.of(rank0, rank1, rank2, rank3, rank4, rank5, rank6, rank7, rank8, rank9);
    }

    public Rank getPlayerRank(Player player) {
        int score = WerewolfBackend.getBackend().getPdc().getScoreOfPlayer(player.getUniqueId());
        return getScoreRank(score);
    }

    public Rank getScoreRank(int score) {
        Map.Entry<Rank, Property<Integer>> bestEntry = Map.entry(Rank.BEGINNER, rank0);
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
            if (stats.getRole() == Role.VAMPIRE) score += 25;
            else if (stats.getRole() == Role.TRAITOR && stats.getKillerId() == null) score += 20;
            else score += 15;
        } else if (stats.getResult() == Result.DEFEAT) {
            if (stats.getRole() == Role.VAMPIRE) score += 10;
            else score += 5;
        }

        score += (stats.getTraitorsGuideUsed() + stats.getDivinationUsed()) * 2;
        score += (stats.getAshUsed() + stats.getRevelationUsed() + stats.getInvisibilityUsed() + stats.getProtectionTriggered() + stats.getSneakNoticeTriggered());
        score += stats.getKills() * 2;

        return score;
    }

    public void assignPrefixSuffix(Player player) {
        int score = WerewolfBackend.getBackend().getPdc().getScoreOfPlayer(player.getUniqueId());
        assignPrefixSuffix(player, score);
    }

    public void assignPrefixSuffix(Player player, int score) {
        Rank rank = getScoreRank(score);
        player.setPlayerListName(rank.getPrefix() + player.getName() + ChatColor.GREEN + " (" + score + ")");
    }

    public void assignRole(Player player, Guild guild) {
        assignRole(WerewolfBackend.getBackend().getPdc().getDiscordIdOfPlayer(player.getUniqueId()), guild, WerewolfGame.getScoreManager().getPlayerRank(player));
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
