package net.aesten.wwrpg.map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class WorldManager {
    private final Map<String, World> worlds = new HashMap<>();

    public WorldManager() {
        loadLobby();
        loadPluginWorlds();
    }

    private void loadLobby() {
        try {
            BufferedReader is = new BufferedReader(new FileReader("server.properties"));
            Properties props = new Properties();
            props.load(is);
            is.close();
            worlds.put("lobby", Bukkit.getWorld(props.getProperty("level-name")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPluginWorlds() {
        String serverDir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        File worldsDir = new File(serverDir + File.separator + "wwrpg_worlds");

        if (!worldsDir.mkdir()) {
            File[] worldDirs = worldsDir.listFiles(File::isDirectory);
            if (worldDirs != null) {
                for (File worldContainer : worldDirs) {
                    createWorld("wwrpg_worlds" + File.separator + worldContainer.getName());
                }
            }
        }
    }

    public World getWorldFromName(String name) {
        return worlds.get(name);
    }

    public void createWorld(String worldName) {
        World world = new WorldCreator(worldName).createWorld();
        if (world != null) {
            worlds.put(world.getName(), world);
        }
    }

    public boolean deleteWorld(World world) {
        if (world == null) return false;
        if (world.getName().equals("lobby")) return false;
        Bukkit.unloadWorld(world, false);
        String serverDir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        File worldDir = new File(serverDir + File.separator + "wwrpg_worlds" + File.separator + world.getName());
        return worldDir.delete();
    }

    public Map<String, World> getWorlds() {
        return worlds;
    }
}