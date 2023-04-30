package net.aesten.werewolfmc.bot.commands.implementations;

import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.bot.commands.BotCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.awt.Color;
import java.util.List;

public class ConfigureCommand extends BotCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.CHANNEL, "voice-channel", "The voice channel used to play the minigame", true, false),
            new OptionData(OptionType.CHANNEL, "log-channel", "The channel which logs game history and bot actions", true, false)
    );

    public ConfigureCommand() {
        super("configure", "Select the voice-channel and log-channel that will be used by the bot", DefaultMemberPermissions.DISABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            Guild guild = event.getGuild();
            assert guild != null;

            OptionMapping vcOpt = event.getOption("voice-channel");
            OptionMapping lcOpt = event.getOption("log-channel");

            if (vcOpt == null || lcOpt == null) {
                event.reply("Missing arguments").setEphemeral(true).queue();
                return;
            }

            String vcId = vcOpt.getAsChannel().asVoiceChannel().getId();
            String lcId = lcOpt.getAsChannel().asTextChannel().getId();

            WerewolfBot.getConfig().getGuildId().set(Long.parseLong(guild.getId()));
            WerewolfBot.getConfig().getVcId().set(Long.parseLong(vcId));
            WerewolfBot.getConfig().getLcId().set(Long.parseLong(lcId));

            createPlayerRoles(guild);

            WerewolfBot.getBot().setConfigured(true);
            event.reply("Successfully updated bot config").setEphemeral(true).queue();
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }

    private void createPlayerRoles(Guild guild) {
        createRoleIfNonExistant(guild, "Werewolf Player", "#3ebe37", true);
        createRoleIfNonExistant(guild, "Beginner", "#11b1e7");
        createRoleIfNonExistant(guild, "Novice", "#11b1e7");
        createRoleIfNonExistant(guild, "Apprentice", "#11b1e7");
        createRoleIfNonExistant(guild, "Intermediate", "#e28621");
        createRoleIfNonExistant(guild, "Skilled", "#e28621");
        createRoleIfNonExistant(guild, "Experienced", "#e28621");
        createRoleIfNonExistant(guild, "Veteran", "#e92269");
        createRoleIfNonExistant(guild, "Expert", "#e92269");
        createRoleIfNonExistant(guild, "Elite", "#e92269");
        createRoleIfNonExistant(guild, "Legendary", "#ffad00");
    }

    private void createRoleIfNonExistant(Guild guild, String name, String colorCode) {
        createRoleIfNonExistant(guild, name, colorCode, false);
    }

    private void createRoleIfNonExistant(Guild guild, String name, String colorCode, boolean hoisted) {
        if (guild.getRolesByName(name, true).size() == 0) {
            guild.createRole().setName(name).setColor(Color.decode(colorCode)).setHoisted(hoisted).complete();
        }
    }
}