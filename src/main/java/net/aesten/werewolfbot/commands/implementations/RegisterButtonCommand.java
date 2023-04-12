package net.aesten.werewolfbot.commands.implementations;

import net.aesten.werewolfbot.commands.BotCommand;
import net.aesten.werewolfrpg.WerewolfRpg;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class RegisterButtonCommand extends BotCommand {
    public RegisterButtonCommand() {
        super("rbutton", "Generates a button to register/unregister on WerewolfRPG", DefaultMemberPermissions.DISABLED, Collections.emptyList());
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            if (!WerewolfRpg.getBot().isSubscribed(Objects.requireNonNull(event.getGuild()))) {
                event.reply("This server is not subscribed, ask an admin to subscribe the server").setEphemeral(true).queue();
                return;
            }

            event.getChannel().sendMessage("To register as a WerewolfRPG player, you have to bind your Discord account to a Minecraft account")
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