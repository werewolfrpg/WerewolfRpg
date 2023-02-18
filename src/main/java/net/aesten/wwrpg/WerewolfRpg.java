package net.aesten.wwrpg;

import com.comphenix.protocol.ProtocolLibrary;
import net.aesten.wwrpg.commands.WerewolfCommand;
import net.aesten.wwrpg.configurations.WerewolfConfig;
import net.aesten.wwrpg.events.WerewolfEvent;
import net.aesten.wwrpg.packets.HideTabListSpectatorsPacket;
import net.azalealibrary.command.AzaleaCommandApi;
import net.azalealibrary.configuration.AzaleaConfigurationApi;
import net.azalealibrary.configuration.FileConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
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
@DependsOn(@Dependency("ProtocolLib"))
public final class WerewolfRpg extends JavaPlugin {
    public static final ChatColor COLOR = ChatColor.GOLD;
    public static final String CHAT_LOG = "[wwrpg] ";

    private static org.bukkit.plugin.Plugin plugin;
    private static HideTabListSpectatorsPacket packetListener;
    private final WerewolfConfig werewolfConfig = new WerewolfConfig();

    @Override
    public void onLoad() {
        plugin = this;
        AzaleaCommandApi.register(this, WerewolfCommand.class);
    }

    @Override
    public void onEnable() {
        //File configurations
        FileConfiguration config = AzaleaConfigurationApi.load(this, werewolfConfig.getName());
        config.load(werewolfConfig);
        AzaleaConfigurationApi.register(werewolfConfig);

        //ProtocolLib
        packetListener = new HideTabListSpectatorsPacket(this);
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);

        //Register event listeners
        getServer().getPluginManager().registerEvents(new WerewolfEvent(), this);
    }

    @Override
    public void onDisable() {
        //File configurations
        FileConfiguration config = AzaleaConfigurationApi.load(this, werewolfConfig.getName());
        config.save(werewolfConfig);

        //ProtocolLib
        ProtocolLibrary.getProtocolManager().removePacketListener(packetListener);
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }
}
