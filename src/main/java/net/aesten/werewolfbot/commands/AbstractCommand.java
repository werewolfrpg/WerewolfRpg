package net.aesten.werewolfbot.commands;


import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public abstract class AbstractCommand implements BotCommand {
    private final String name;
    private final String description;
    private final boolean isGuildOnly;
    private final DefaultMemberPermissions permissions;

    public AbstractCommand(String name, String description, boolean isGuildOnly, DefaultMemberPermissions permissions) {
        this.name = name;
        this.description = description;
        this.isGuildOnly = isGuildOnly;
        this.permissions = permissions;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isGuildOnly() {
        return isGuildOnly;
    }

    public DefaultMemberPermissions getPermissions() {
        return permissions;
    }

    @Override
    public CommandData getCommand() {
        return Commands.slash(name, description).setDefaultPermissions(permissions).setGuildOnly(isGuildOnly);
    }
}
