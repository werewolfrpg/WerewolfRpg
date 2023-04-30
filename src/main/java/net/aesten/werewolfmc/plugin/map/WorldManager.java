package net.aesten.werewolfmc.plugin.map;

import net.aesten.werewolfmc.plugin.utilities.WerewolfUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
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
            World lobby = Bukkit.getWorld(props.getProperty("level-name"));
            worlds.put("lobby", lobby);
            assert lobby != null;
            setGameRule(lobby);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPluginWorlds() {
        String serverDir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        File worldsDir = new File(serverDir + File.separator + "wwmc_worlds");

        if (!worldsDir.mkdir()) {
            File[] worldDirs = worldsDir.listFiles(File::isDirectory);
            if (worldDirs != null) {
                for (File worldContainer : worldDirs) {
                    loadWorld(worldContainer.getName());
                }
            }
        }
    }

    public World getWorldFromName(String name) {
        return worlds.get(name);
    }

    public World getLobby() {
        return worlds.get("lobby");
    }

    public void loadWorld(String worldName) {
        World world = new WorldCreator("wwmc_worlds/" + worldName).createWorld();
        if (world != null) {
            setGameRule(world);
            worlds.put(world.getName(), world);
        }
    }

    public boolean deleteWorld(String worldName) {
        if (worldName.equals("lobby")) return false;
        World world = worlds.get(worldName);
        if (world == null) return false;
        Bukkit.unloadWorld(world, false);
        String serverDir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        File worldDir = new File(serverDir + File.separator + world.getName());
        worlds.remove(worldName);
        return WerewolfUtil.deleteDirectory(worldDir);
    }

    public Map<String, World> getWorlds() {
        return worlds;
    }

    public boolean worldContainerExists(String folderName) {
        return new File("wwmc_worlds" + File.separator + folderName).exists();
    }

    private void setGameRule(World world) {
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false);
        world.setGameRule(GameRule.DO_MOB_LOOT, false);
        world.setGameRule(GameRule.DO_ENTITY_DROPS, false);
        world.setGameRule(GameRule.DO_TILE_DROPS, false);
        world.setGameRule(GameRule.DISABLE_RAIDS, true);
        world.setGameRule(GameRule.DO_FIRE_TICK, false);
        world.setGameRule(GameRule.DO_PATROL_SPAWNING, false);
        world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        world.setGameRule(GameRule.DO_TRADER_SPAWNING, false);
        world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
        world.setGameRule(GameRule.KEEP_INVENTORY, true);
        world.setGameRule(GameRule.MOB_GRIEFING, false);
        world.setGameRule(GameRule.COMMAND_BLOCK_OUTPUT, false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        world.setGameRule(GameRule.DISABLE_ELYTRA_MOVEMENT_CHECK, true);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION, true);
        world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
    }
}
