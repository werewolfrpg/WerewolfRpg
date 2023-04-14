package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.BotCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.aesten.werewolfdb.QueryManager;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.*;
import java.util.List;
import java.util.Objects;

public class SubscribeCommand extends BotCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.CHANNEL, "voice-channel", "The voice channel used to play the minigame", true, false),
            new OptionData(OptionType.CHANNEL, "log-channel", "The channel which logs game history and bot actions", true, false)
    );

    public SubscribeCommand() {
        super("subscribe", "Register the server for the bot to enable other commands", DefaultMemberPermissions.DISABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            OptionMapping vcOpt = event.getOption("voice-channel");
            OptionMapping lcOpt = event.getOption("log-channel");

            if (vcOpt == null || lcOpt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            String vcId = vcOpt.getAsChannel().asVoiceChannel().getId();
            String lcId = lcOpt.getAsChannel().asTextChannel().getId();

            if (WerewolfRpg.getBot().getJda().getGuildChannelById(vcId) == null || WerewolfRpg.getBot().getJda().getGuildChannelById(lcId) == null) {
                event.reply("Invalid channels").queue();
                return;
            }

            if (WerewolfRpg.getBot().isSubscribed(Objects.requireNonNull(event.getGuild()))) {
                event.reply("This guild is already registered").queue();
            } else {
                subscribe(event.getGuild(), vcId, lcId);
                event.reply("Subscription succeeded").queue();
            }
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }

    private void subscribe(Guild guild, String vcId, String lcId) {
        WerewolfRpg.getBot().getSubscribedGuilds().add(guild.getId());
        QueryManager.addGuild(guild.getId(), vcId, lcId);
        createPlayerRoles(guild);
    }

    private void createPlayerRoles(Guild guild) {
        guild.createRole()
                .setName("Werewolf RPG")
                .setColor(Color.decode("#3ebe37"))
                .setHoisted(true)
                .complete();

        guild.createRole()
                .setName("Beginner")
                .setColor(Color.decode("#11b1e7"))
                .complete();
        guild.createRole()
                .setName("Novice")
                .setColor(Color.decode("#11b1e7"))
                .complete();
        guild.createRole()
                .setName("Apprentice")
                .setColor(Color.decode("#11b1e7"))
                .complete();

        guild.createRole()
                .setName("Intermediate")
                .setColor(Color.decode("#e28621"))
                .complete();
        guild.createRole()
                .setName("Skilled")
                .setColor(Color.decode("#e28621"))
                .complete();
        guild.createRole()
                .setName("Experienced")
                .setColor(Color.decode("#e28621"))
                .complete();

        guild.createRole()
                .setName("Veteran")
                .setColor(Color.decode("#e92269"))
                .complete();
        guild.createRole()
                .setName("Expert")
                .setColor(Color.decode("#e92269"))
                .complete();
        guild.createRole()
                .setName("Elite")
                .setColor(Color.decode("#e92269"))
                .complete();

        guild.createRole()
                .setName("Legendary")
                .setColor(Color.decode("#ffad00"))
                .complete();
    }
}