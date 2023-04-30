package net.aesten.werewolfmc.bot.commands.implementations;

import net.aesten.werewolfmc.bot.commands.BotCommand;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.List;

public class RegisterButtonCommand extends BotCommand {
    public RegisterButtonCommand() {
        super("rbutton", "Generates a button to register/unregister on the Werewolf", DefaultMemberPermissions.DISABLED, Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            event.getChannel().sendMessage("To register as a Werewolf player, you have to bind your Discord account to a Minecraft account")
                    .addActionRow(
                            Button.success("register-button", "Register"),
                            Button.danger("unregister-button", "Unregister"))
                    .queue();

            event.reply("You have now enabled anyone on the server to register as a player").setEphemeral(true).queue();
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }
}
