package net.aesten.werewolfmc.bot.commands.implementations;

import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.commands.BotCommand;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

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
            OptionMapping userOpt = event.getOption("user");
            OptionMapping actionOpt = event.getOption("action");
            OptionMapping valueOpt = event.getOption("value");

            if (userOpt == null || actionOpt == null || valueOpt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            WerewolfBackend backend = WerewolfBackend.getBackend();
            User user = userOpt.getAsUser();
            String action = actionOpt.getAsString();
            int value = valueOpt.getAsInt();

            if (action.equals("add")) {
                UUID mcId = backend.getPdc().getMinecraftIdFromDiscordId(user.getIdLong()).join();
                int score = backend.getPdc().addScoreToPlayer(mcId, value).join().getScore();
                applyRank(mcId, score, user, event.getGuild());
                event.reply(user.getName() + " now has " + score + " score").setEphemeral(true).queue();
            } else if (action.equals("set")) {
                UUID mcId = backend.getPdc().getMinecraftIdFromDiscordId(user.getIdLong()).join();
                backend.getPdc().setScoreOfPlayer(mcId, value).join();
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

    private void applyRank(UUID mcId, int value, User user, Guild guild) {
        WerewolfGame.getScoreManager().assignRole(user.getIdLong(), guild, WerewolfGame.getScoreManager().getScoreRank(value));
        if (Bukkit.getOnlinePlayers().stream().map(Entity::getUniqueId).toList().contains(mcId)) {
            WerewolfGame.getScoreManager().assignPrefixSuffix(Objects.requireNonNull(Bukkit.getPlayer(mcId)), value);
        }
    }
}
