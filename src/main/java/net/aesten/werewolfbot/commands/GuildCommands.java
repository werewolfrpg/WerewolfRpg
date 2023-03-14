package net.aesten.werewolfbot.commands;

import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

public class GuildCommands {
    public static final CommandData registrationButtonCommand = Commands
            .slash("rbutton", "creates a button to register players")
            .setDefaultPermissions(DefaultMemberPermissions.DISABLED)
            .setGuildOnly(true);

    public static final CommandData unregisterCommand = Commands
            .slash("unregister", "unregisters the user from players")
            .setDefaultPermissions(DefaultMemberPermissions.ENABLED)
            .setGuildOnly(true);
}
