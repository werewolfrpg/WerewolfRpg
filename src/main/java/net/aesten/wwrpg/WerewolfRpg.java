package net.aesten.wwrpg;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import net.aesten.wwrpg.events.WerewolfEvent;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.dependency.Dependency;
import org.bukkit.plugin.java.annotation.dependency.DependsOn;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;


@Plugin(name = "WerewolfRPG", version = "2.0")
@Description("This mini-game is an adaptation of the \"Werewolf\" designed to be played on Minecraft with some additional RPG elements.")
@Author("Aesten")
@LogPrefix("wwrpg")
@DependsOn(@Dependency("ProtocolLib"))
@Command(name = "ww", desc = "Master command for all WerewolfRPG-related commands", aliases = {"wwrpg", "werewolf"}, usage = "/<command> <subcommand>")
public final class WerewolfRpg extends JavaPlugin {
    private static org.bukkit.plugin.Plugin plugin;
    private static ProtocolManager protocolManager;
    public static final ChatColor COLOR = ChatColor.GOLD;
    public static final String LOG = "[wwrpg]: ";



    @Override
    public void onLoad() {
        plugin = this;
        protocolManager = ProtocolLibrary.getProtocolManager();
        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.BLUE + "Plugin loaded");
    }

    @Override
    public void onEnable() {
        getConfig().addDefault("config.something", "default");
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new WerewolfEvent(), this);

        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.GREEN + "Plugin enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.RED + "Plugin disabled");
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}
