package net.aesten.werewolfmc;

import com.comphenix.protocol.ProtocolLibrary;
import net.aesten.werewolfmc.backend.WerewolfBackend;
import net.aesten.werewolfmc.bot.WerewolfBot;
import net.aesten.werewolfmc.plugin.commands.WerewolfCommand;
import net.aesten.werewolfmc.plugin.commands.ConsoleCommand;
import net.aesten.werewolfmc.plugin.packets.SpecInfoPacket;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.events.GeneralEvents;
import net.azalealibrary.command.AzaleaCommandApi;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.azalealibrary.configuration.Configurable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.plugin.ApiVersion;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "Werewolf", version = "2.2")
@Description("This mini-game is an adaptation of the \"Werewolf\" designed to be played on Minecraft with some additional action elements.")
@Author("Aesten")
@LogPrefix("Werewolf")
@ApiVersion(ApiVersion.Target.v1_19)
@Dependency("AzaleaConfiguration")
@Dependency("AzaleaCommand")
@Dependency("ProtocolLib")
public final class WerewolfPlugin extends JavaPlugin {
    public static final ChatColor COLOR = ChatColor.GOLD;
    public static final String CHAT_LOG = "[Werewolf] ";

    private static org.bukkit.plugin.Plugin plugin;

    private SpecInfoPacket specInfoPacket;

    @Override
    public void onLoad() {
        logConsole("Loading the Werewolf Plugin");
        plugin = this;
    }

    @Override
    public void onEnable() {
        //plugin configuration
        Configurable config = WerewolfGame.getConfig();
        AzaleaConfigurationApi.load(this, config);

        //register commands
        AzaleaCommandApi.register(this, new WerewolfCommand());
        AzaleaCommandApi.register(this, new ConsoleCommand());

        //load worlds & maps
        WerewolfGame.initMapManager();

        //register event listeners
        getServer().getPluginManager().registerEvents(new GeneralEvents(), this);

        //protocol manager initialization
        specInfoPacket = new SpecInfoPacket(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(specInfoPacket);

        //enable backend if configured
        WerewolfBackend.init();

        //enable discord bot if configured
        WerewolfBot.init();
    }

    @Override
    public void onDisable() {
        //plugin configuration
        Configurable config = WerewolfGame.getConfig();
        AzaleaConfigurationApi.save(this, config);

        WerewolfGame.getMapManager().saveMaps();

        //unregister teams
        WerewolfGame.getTeamsManager().unregisterTeams();

        //unregister packet
        ProtocolLibrary.getProtocolManager().removePacketListener(specInfoPacket);

        //shutdown backend
        WerewolfBackend.shutDown();

        //shutdown bot
        WerewolfBot.shutDown();
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }

    public static void logConsole(String log) {
        Bukkit.getServer().getConsoleSender().sendMessage(CHAT_LOG + log);
    }
}