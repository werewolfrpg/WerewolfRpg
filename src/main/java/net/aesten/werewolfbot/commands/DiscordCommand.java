package net.aesten.werewolfbot.commands;



import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public abstract class DiscordCommand implements BotCommand {
    private final String name;
    private final String description;
    private final DefaultMemberPermissions permissions;
    private final List<OptionData> options;

    public DiscordCommand(String name, String description, DefaultMemberPermissions permissions, List<OptionData> options) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
        this.options = options;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(name, description).setDefaultPermissions(permissions).setGuildOnly(true).addOptions(options);
    }
}
