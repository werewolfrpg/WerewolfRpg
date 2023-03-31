package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.WerewolfBot;
import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SessionCommand extends DiscordCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.STRING, "action", "Begin or end a session", true, true)
    );

    public SessionCommand() {
        super("session", "Start and stop game sessions in the current server", DefaultMemberPermissions.DISABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            OptionMapping opt = event.getOption("action");

            if (opt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            String action = opt.getAsString();
            WerewolfBot bot = WerewolfRpg.getBot();

            if (action.equals("begin") && bot.getCurrentSession() == null) {
                String vcId = QueryManager.requestGuildVoiceChannel(Objects.requireNonNull(event.getGuild()).getId());
                String lcId = QueryManager.requestGuildLogChannel(Objects.requireNonNull(event.getGuild()).getId());
                VoiceChannel vc = event.getGuild().getVoiceChannelById(vcId);
                TextChannel lc = event.getGuild().getTextChannelById(lcId);

                if (vc == null || lc == null) {
                    event.reply("An error occurred while passing the vc and lc id to the session initializer").queue();
                    return;
                }

                bot.newSession(vc, lc);
                event.reply("Session started").queue();
            } else if (action.equals("end") && bot.getCurrentSession() != null) {
                bot.endSession();
                event.reply("Session terminated").queue();
            } else {
                event.reply("Could not execute command").queue();
            }
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return new ArrayList<>(List.of("begin", "end"));
    }
}