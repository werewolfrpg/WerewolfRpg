package net.aesten.werewolfbot;

import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.sql.Timestamp;
import java.time.Instant;

public class Session {
    private final VoiceChannel vc;
    private final TextChannel lc;
    private final Timestamp timestamp;

    public Session(VoiceChannel vc, TextChannel lc) {
        this.vc = vc;
        this.lc = lc;
        this.timestamp = Timestamp.from(Instant.now());
        init();
    }

    public VoiceChannel getVc() {
        return vc;
    }

    public TextChannel getLc() {
        return lc;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    private void init() {
        lc.sendMessage("**NEW SESSION" + timestamp + "**").queue();
    }

    public void terminate() {
        lc.sendMessage("**END SESSION" + Timestamp.from(Instant.now()) + "**").queue();
        vc.getMembers().forEach(member -> member.mute(false).queue());
    }
}
