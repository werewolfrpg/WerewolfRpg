package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.BotCommand;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.statistics.Rank;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ScoreCommand extends BotCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.USER, "user", "The player to modify the score", true, false),
            new OptionData(OptionType.STRING, "action", "The action to apply to a player's score", true, true),
            new OptionData(OptionType.INTEGER, "value", "The modifying value of the player's score", true, false)
    );

    public ScoreCommand() {
        super("score", "Modify the score of players", DefaultMemberPermissions.DISABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            if (!WerewolfRpg.getBot().isSubscribed(Objects.requireNonNull(event.getGuild()))) {
                event.reply("This server is not subscribed, ask an admin to subscribe the server").setEphemeral(true).queue();
                return;
            }

            OptionMapping userOpt = event.getOption("user");
            OptionMapping actionOpt = event.getOption("action");
            OptionMapping valueOpt = event.getOption("value");

            if (userOpt == null || actionOpt == null || valueOpt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            User user = userOpt.getAsUser();
            String action = actionOpt.getAsString();
            int value = valueOpt.getAsInt();

            if (action.equals("add")) {
                String mcId = QueryManager.getMcIdOfDiscordUser(user.getId());
                int score = QueryManager.addPlayerScore(mcId, value);
                applyRank(mcId, score, user, event.getGuild());
                event.reply(user.getName() + " now has " + score + " score").setEphemeral(true).queue();
            } else if (action.equals("set")) {
                String mcId = QueryManager.getMcIdOfDiscordUser(user.getId());
                QueryManager.setPlayerScore(mcId, value);
                applyRank(mcId, value, user, event.getGuild());
                event.reply(user.getName() + " now has " + value + " score").setEphemeral(true).queue();
            } else {
                event.reply("Could not resolve command arguments").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        if (event.isFromGuild()) {
            if (event.getFocusedOption().getName().equals("action")) {
                return List.of("add", "set");
            }
        }
        return null;
    }

    private void applyRank(String mcId, int value, User user, Guild guild) {
        Rank rank = WerewolfGame.getScoreManager().getScoreRank(value);
        WerewolfGame.getScoreManager().assignRole(user.getId(), guild, rank);
        if (WerewolfRpg.getPlugin().getServer().getOnlinePlayers().stream().map(Entity::getUniqueId).toList().contains(UUID.fromString(mcId))) {
            WerewolfGame.getScoreManager().assignPrefix(Objects.requireNonNull(Bukkit.getPlayer(UUID.fromString(mcId))), rank);
        }
    }
}
