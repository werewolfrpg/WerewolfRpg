package net.aesten.werewolfbot.data;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.ArrayList;
import java.util.List;

public class Session {
    private final Guild guild;
    private final VoiceChannel vc;
    private final List<String> matchIds = new ArrayList<>();

    public Session(Guild guild, VoiceChannel vc) {
        this.guild = guild;
        this.vc = vc;
    }

    public Guild getGuild() {
        return guild;
    }

    public VoiceChannel getVc() {
        return vc;
    }

    public List<String> getMatchIds() {
        return matchIds;
    }
}
