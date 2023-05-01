package net.aesten.werewolfmc.plugin.utilities;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.data.WerewolfPlayerData;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Skull;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.Objects;

public class WerewolfUtil {
    public static void sendPluginText(CommandSender sender, String message) {
        sendPluginText(sender, message, ChatColor.AQUA);
    }

    public static void sendPluginText(CommandSender sender, String message, ChatColor color) {
        sender.sendMessage(net.aesten.werewolfmc.WerewolfPlugin.COLOR + net.aesten.werewolfmc.WerewolfPlugin.CHAT_LOG + color + message);
    }

    public static void sendHelpText(CommandSender sender, String message) {
        sendPluginText(sender, message, ChatColor.YELLOW);
    }

    public static void sendErrorText(CommandSender sender, String message) {
        sendPluginText(sender, message, ChatColor.RED);
    }

    public static void sendTitle(Player player, String title, String subtitle) {
        player.sendTitle(title, subtitle, 2, 2, 40);
    }

    public static Vector getVectorCenterSpawn(Location blockLocation) {
        return new Vector(blockLocation.getX(), blockLocation.getY(), blockLocation.getZ()).add(new Vector(0.5, 1, 0.5));
    }

    public static void updateSkull(World world, Vector skullCoordinates, Player player) {
        Block block = world.getBlockAt(skullCoordinates.toLocation(world));
        BlockFace rotation = ((Rotatable) block.getBlockData()).getRotation();
        block.setType(Material.PLAYER_HEAD);
        if (block.getState() instanceof Skull skull) {
            skull.setOwningPlayer(player);
            Rotatable rotatable = (Rotatable) skull.getBlockData();
            rotatable.setRotation(rotation);
            skull.setBlockData(rotatable);
            skull.update();
        }
    }

    public static void resetSkull(World world, Vector skullCoordinates) {
        Block block = world.getBlockAt(skullCoordinates.toLocation(world));
        BlockFace rotation = ((Rotatable) block.getBlockData()).getRotation();
        block.setType(Material.SKELETON_SKULL);
        Rotatable rotatable = (Rotatable) block.getBlockData();
        rotatable.setRotation(rotation);
        block.setBlockData(rotatable);

    }

    public static ArmorStand summonNameTagArmorStand(World world, Vector coordinates, Vector offset, String name) {
        ArmorStand armorStand = (ArmorStand) world.spawnEntity(coordinates.clone().add(offset).toLocation(world), EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setVisible(false);
        armorStand.setInvulnerable(true);
        armorStand.setGravity(false);
        armorStand.setSmall(true);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(ChatColor.AQUA + name);
        return armorStand;
    }

    public static void removeAllPotionEffectsFromPlayer(Player player) {
        for (PotionEffectType type : PotionEffectType.values()) {
            if (player.hasPotionEffect(type)) {
                player.removePotionEffect(type);
            }
        }
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound , 0.6f, 1);
    }

    public static void runDelayedTask(int delay, Runnable runnable) {
        new BukkitRunnable() {
            @Override
            public void run() {
                runnable.run();
            }
        }.runTaskLater(net.aesten.werewolfmc.WerewolfPlugin.getPlugin(), delay);
    }

    public static boolean areSameFaction(Role role1, Role role2) {
        return role1.factionRole() == role2.factionRole();
    }

    public static boolean sameItem(ItemStack item1, ItemStack item2) {
        return (Objects.requireNonNull(item1.getItemMeta()).getAsString().equals(Objects.requireNonNull(item2.getItemMeta()).getAsString()));
    }

    public static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public static void applyPacketGlowing(Player viewer, WerewolfGame game) {
        int duration = 2400;
        for (Player player : game.getParticipants()) {
            WerewolfPlayerData data = game.getDataMap().get(player.getUniqueId());
            if (data != null && data.isAlive() && data.getRole() != Role.WEREWOLF) {
                // create the metadata packet
                PacketContainer metadataPacket = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);

                // get the data watcher for the entity
                WrappedDataWatcher dataWatcher = WrappedDataWatcher.getEntityWatcher(player);

                // add a new data watcher item for the glowing effect with the specified duration
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(0, WrappedDataWatcher.Registry.get(Byte.class)), (byte) 0x40);
                dataWatcher.setObject(new WrappedDataWatcher.WrappedDataWatcherObject(11, WrappedDataWatcher.Registry.get(Integer.class)), duration);

                // set the entity ID and modified data watcher for the metadata packet
                metadataPacket.getIntegers().write(0, player.getEntityId());
                metadataPacket.getWatchableCollectionModifier().write(0, dataWatcher.getWatchableObjects());

                // send the packet to the specific player
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(viewer, metadataPacket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
