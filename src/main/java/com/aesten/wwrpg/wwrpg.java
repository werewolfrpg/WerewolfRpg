package com.aesten.wwrpg;

import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.annotation.command.Command;
import org.bukkit.plugin.java.annotation.permission.ChildPermission;
import org.bukkit.plugin.java.annotation.permission.Permission;
import org.bukkit.plugin.java.annotation.plugin.Description;
import org.bukkit.plugin.java.annotation.plugin.LogPrefix;
import org.bukkit.plugin.java.annotation.plugin.Plugin;
import org.bukkit.plugin.java.annotation.plugin.author.Author;

@Plugin(name = "wwrpg", version = "3.0")
@Description("This mini-game is an adaptation of the \"Werewolf\" designed to be played on Minecraft with some additional RPG elements.")
@Author("Aesten")
@LogPrefix("wwrpg")
@Command(name = "foo", desc = "Foo command", aliases = {"foobar", "fubar"}, permission = "test.foo", permissionMessage = "You do not have permission!", usage = "/<command> [test|stop]")
@Permission(name = "test.foo", desc = "Allows foo command", defaultValue = PermissionDefault.OP)
@Permission(name = "test.*", desc = "Wildcard permission", children = {@ChildPermission(name ="test.foo")})
public final class wwrpg extends JavaPlugin {
    private static org.bukkit.plugin.Plugin plugin;
    private static final ChatColor COLOR = ChatColor.GOLD;
    private static final String LOG = "[wwrpg]: ";


    @Override
    public void onLoad() {
        plugin = this;
        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.BLUE + "Plugin loaded");
    }

    @Override
    public void onEnable() {
        getConfig().addDefault("config.something", "default");
        saveDefaultConfig();

        getServer().getPluginManager().registerEvents(new Event(), this);

        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.GREEN + "Plugin enabled");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(COLOR + LOG + ChatColor.RED + "Plugin disabled");
    }

    public static org.bukkit.plugin.Plugin getPlugin() {
        return plugin;
    }
}
