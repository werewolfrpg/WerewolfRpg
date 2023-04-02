package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.BotCommand;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReadRulesCommand extends BotCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.STRING, "language", "Language to print the rules in", true, true)
    );

    public ReadRulesCommand() {
        super("rules", "Returns link to access rules in specified language", DefaultMemberPermissions.DISABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            if (!WerewolfRpg.getBot().isSubscribed(Objects.requireNonNull(event.getGuild()))) {
                event.reply("This server is not subscribed, ask an admin to subscribe the server").setEphemeral(true).queue();
                return;
            }

            OptionMapping opt = event.getOption("language");

            if (opt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            String language = opt.getAsString();
            event.reply("https://github.com/werewolfrpg/WerewolfRpg/tree/master/rules/" + language + ".md").setEphemeral(true).queue();
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return new ArrayList<>(List.of("en_US", "fr_FR"));
    }
}
