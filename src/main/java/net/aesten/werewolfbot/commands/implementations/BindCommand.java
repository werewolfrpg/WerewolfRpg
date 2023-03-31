package net.aesten.werewolfbot.commands.implementations;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.aesten.werewolfbot.commands.DiscordCommand;
import net.aesten.werewolfdb.QueryManager;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class BindCommand extends DiscordCommand {
    private static final List<OptionData> options = List.of(
            new OptionData(OptionType.STRING, "mcid", "Minecraft ID", true, false)
    );

    public BindCommand() {
        super("bind", "Links your discord account to your Minecraft ID", DefaultMemberPermissions.ENABLED, options);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.isFromGuild()) {
            OptionMapping opt = event.getOption("mcid");

            if (opt == null) {
                event.reply("Missing arguments").queue();
                return;
            }

            String mcid = opt.getAsString();
            String uuid;

            try {
                URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + mcid);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(response.toString(), JsonObject.class);

                uuid = jsonObject.get("id").getAsString();
            } catch (IOException e) {
                event.reply("Could not execute command").setEphemeral(true).queue();
                return;
            }

            QueryManager.addIdBinding(uuid, event.getUser().getId());
            event.reply("Account binding succeeded").setEphemeral(true).queue();
        }
    }

    @Override
    public List<String> complete(CommandAutoCompleteInteractionEvent event) {
        return null;
    }
}
