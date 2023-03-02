package net.aesten.wwrpg;

import com.comphenix.protocol.ProtocolLibrary;
import net.aesten.wwrpg.commands.WerewolfCommand;
import net.aesten.wwrpg.commands.subcommands.*;
import net.aesten.wwrpg.core.WerewolfGame;
import net.aesten.wwrpg.events.GeneralEvents;
import net.aesten.wwrpg.packets.HideTabListSpectatorsPacket;
import net.azalealibrary.command.AzaleaCommandApi;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.azalealibrary.configuration.Configurable;
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
@ApiVersion(ApiVersion.Target.v1_17)
@Dependency("ProtocolLib")
@Dependency("AzaleaConfiguration")
@Dependency("AzaleaCommand")
public final class WerewolfRpg extends JavaPlugin {
    public static final ChatColor COLOR = ChatColor.GOLD;
    public static final String CHAT_LOG = "[wwrpg] ";

    private static org.bukkit.plugin.Plugin plugin;
    private static HideTabListSpectatorsPacket packetListener;

    @Override
    public void onLoad() {
        plugin = this;

        //Register commands
        AzaleaCommandApi.register(this, WerewolfCommand.class);
    }

    @Override
    public void onEnable() {
        //File configurations
        Configurable shop = WerewolfGame.getShopManager();
        AzaleaConfigurationApi.load(this, shop);

        Configurable skeleton = WerewolfGame.getSkeletonManager();
        AzaleaConfigurationApi.load(this, skeleton);

        //load worlds & maps
        WerewolfGame.initMapManager();

        //ProtocolLib
        packetListener = new HideTabListSpectatorsPacket(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);

        //Register event listeners
        getServer().getPluginManager().registerEvents(new GeneralEvents(), this);
    }

    @Override
    public void onDisable() {
        //File configurations
        Configurable shop = WerewolfGame.getShopManager();
        AzaleaConfigurationApi.save(this, shop);

        Configurable skeleton = WerewolfGame.getSkeletonManager();
        AzaleaConfigurationApi.save(this, skeleton);

        //ProtocolLib
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }
}
