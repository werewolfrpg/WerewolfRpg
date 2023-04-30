package net.aesten.werewolfmc.bot;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.backend.models.PlayerData;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RegistrationListener extends ListenerAdapter {
    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        WerewolfBackend backend = WerewolfBackend.getBackend();
        if (event.getComponentId().equals("register-button")) {
            if (backend.getPdc().getMinecraftIdFromDiscordId(event.getUser().getIdLong()).join() != null) {
                event.reply("You already have a registered Minecraft ID").setEphemeral(true).queue();
            } else {
                TextInput subject = TextInput.create("minecraft-id", "Minecraft ID", TextInputStyle.SHORT)
                        .setPlaceholder("MyMinecraftUserName123")
                        .setRequiredRange(3, 16)
                        .build();

                Modal modal = Modal.create("registration", "Registration Form")
                        .addComponents(ActionRow.of(subject))
                        .build();

                event.replyModal(modal).queue();
            }
        } else if (event.getComponentId().equals("unregister-button")) {
            if (backend.getPdc().getMinecraftIdFromDiscordId(event.getUser().getIdLong()).join() == null) {
                event.reply("You are not registered").setEphemeral(true).queue();
            } else {
                backend.getPdc().deletePlayerByDiscordId(event.getUser().getIdLong()).join();

                List<Role> role = Objects.requireNonNull(event.getGuild()).getRolesByName("Werewolf mc", true);
                if (role.size() != 0) {
                    Objects.requireNonNull(event.getGuild()).removeRoleFromMember(event.getUser(), role.get(0)).submit();
                }

                WerewolfGame.getScoreManager().getRankRoles(event.getGuild()).forEach(r -> event.getGuild().removeRoleFromMember(event.getUser(), r).submit());

                event.reply("Your registration has been successfully canceled").setEphemeral(true).queue();
            }
        }
    }

    @Override
    public void onModalInteraction(@Nonnull ModalInteractionEvent event) {
        if (event.getModalId().equals("registration")) {
            WerewolfBackend backend = WerewolfBackend.getBackend();
            String mcid = Objects.requireNonNull(event.getValue("minecraft-id")).getAsString();
            UUID uuid = getUUID(mcid);

            if (uuid == null) {
                event.reply("Account not found...").setEphemeral(true).queue();
            } else if (backend.getPdc().getAllMinecraftIds().join().contains(uuid)) {
                event.reply("Your Minecraft ID account is already registered").setEphemeral(true).queue();
            } else {
                backend.getPdc().registerPlayer(new PlayerData(uuid, event.getUser().getIdLong(), 0)).join();
                List<Role> werewolfRole = Objects.requireNonNull(event.getGuild()).getRolesByName("Werewolf mc", true);
                if (werewolfRole.size() != 0) {
                    Objects.requireNonNull(event.getGuild()).addRoleToMember(event.getUser(), werewolfRole.get(0)).submit();
                }
                List<Role> beginnerRole = Objects.requireNonNull(event.getGuild()).getRolesByName("Beginner", true);
                if (beginnerRole.size() != 0) {
                    Objects.requireNonNull(event.getGuild()).addRoleToMember(event.getUser(), beginnerRole.get(0)).submit();
                }

                event.reply("You are now registered!").setEphemeral(true).queue();
                net.aesten.werewolfmc.WerewolfPlugin.logConsole("Player " + mcid + " (" + uuid + ") registered in server: " + event.getGuild().getName());
            }
        }
    }

    private UUID getUUID(String mcid) {
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
            return UUID.fromString(jsonObject.get("id").getAsString().replaceAll("(.{8})(.{4})(.{4})(.{4})(.+)", "$1-$2-$3-$4-$5"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
