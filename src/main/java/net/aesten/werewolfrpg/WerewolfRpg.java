package net.aesten.werewolfrpg;

import com.comphenix.protocol.ProtocolLibrary;
import net.aesten.werewolfbot.WerewolfBot;
import net.aesten.werewolfrpg.packets.SpecInfoPacket;
import net.aesten.werewolfrpg.commands.WerewolfCommand;
import net.aesten.werewolfrpg.core.WerewolfGame;
import net.aesten.werewolfrpg.events.GeneralEvents;
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

import java.io.*;

@Plugin(name = "WerewolfRPG", version = "2.0")
@Description("This mini-game is an adaptation of the \"Werewolf\" designed to be played on Minecraft with some additional RPG elements.")
@Author("Aesten")
@LogPrefix("WerewolfRPG")
@ApiVersion(ApiVersion.Target.v1_19)
@Dependency("AzaleaConfiguration")
@Dependency("AzaleaCommand")
public final class WerewolfRpg extends JavaPlugin {
    public static final ChatColor COLOR = ChatColor.GOLD;
    public static final String CHAT_LOG = "[wwrpg] ";

    private static org.bukkit.plugin.Plugin plugin;
    private static WerewolfBot bot;

    private SpecInfoPacket specInfoPacket;

    @Override
    public void onLoad() {
        plugin = this;

        //Register commands
        AzaleaCommandApi.register(this, WerewolfCommand.class);
    }

    @Override
    public void onEnable() {
        //plugin configurations
        Configurable shop = WerewolfGame.getShopManager();
        AzaleaConfigurationApi.load(this, shop);

        Configurable skeleton = WerewolfGame.getSkeletonManager();
        AzaleaConfigurationApi.load(this, skeleton);

        //load worlds & maps
        WerewolfGame.initMapManager();

        //register event listeners
        getServer().getPluginManager().registerEvents(new GeneralEvents(), this);

        //protocol manager initialization
        specInfoPacket = new SpecInfoPacket(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(specInfoPacket);

        //enable discord bot if configured
        initBot();
    }

    @Override
    public void onDisable() {
        //plugin configurations
        Configurable shop = WerewolfGame.getShopManager();
        AzaleaConfigurationApi.save(this, shop);

        Configurable skeleton = WerewolfGame.getSkeletonManager();
        AzaleaConfigurationApi.save(this, skeleton);

        WerewolfGame.getMapManager().saveMaps();

        //unregister teams
        WerewolfGame.getTeamsManager().unregisterAll();

        //unregister packet
        ProtocolLibrary.getProtocolManager().removePacketListener(specInfoPacket);

        //shutdown bot
        shutDownBot();
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }

    public static WerewolfBot getBot() {
        return bot;
    }

    public static void logConsole(String log) {
        Bukkit.getServer().getConsoleSender().sendMessage("[WerewolfRPG] " + log);
    }

    private String getBotToken() throws IOException {
        File file = new File(this.getDataFolder() + File.separator + "wwrpg-bot-token.txt");
        if (file.createNewFile()) {
            logConsole("File wwrpg-bot-token.txt not found, created one in plugin data folder");
            return "";
        } else {
            return new BufferedReader(new FileReader(file)).readLine();
        }
    }

    private void initBot() {
        try {
            String token = getBotToken();
            if (token.equals("")) {
                logConsole("Discord bot not enabled");
                bot = null;
            } else {
                bot = new WerewolfBot(token);
            }
        } catch (IOException e) {
            logConsole("Failed to read bot token");
        }
    }

    private void shutDownBot() {
        if (bot != null) {
            logConsole("Shutting down discord bot");
            bot.getJda().shutdown();
        }
    }
}

//todo later: add a prompt to create roles and assign 'MMR' value to get role

