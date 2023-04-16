package net.aesten.werewolfrpg.bot.commands.implementations;

import net.aesten.werewolfrpg.bot.commands.BotCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;

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
            OptionMapping opt = event.getOption("language");
            assert opt != null;

            String language = opt.getAsString();
            event.reply("https://github.com/werewolfrpg/WerewolfRpg/tree/master/rules/" + language + ".md").setEphemeral(true).queue();
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return new ArrayList<>(List.of("en_US", "fr_FR"));
    }
}
