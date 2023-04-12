package net.aesten.werewolfrpg;

import com.comphenix.protocol.ProtocolLibrary;
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
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }


    public static void logConsole(String log) {
        Bukkit.getServer().getConsoleSender().sendMessage("[WerewolfRPG] " + log);
    }
}
