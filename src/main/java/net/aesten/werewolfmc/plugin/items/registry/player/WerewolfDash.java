package net.aesten.werewolfmc.plugin.items.registry.player;

import net.aesten.werewolfmc.plugin.items.base.InteractItem;
import net.aesten.werewolfmc.plugin.items.base.ItemStackBuilder;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class WerewolfDash extends WerewolfItem implements InteractItem {
    @Override
    public String getId() {
        return "werewolf_dash";
    }

    @Override
    protected ItemStack getBaseItem() {
        return ItemStackBuilder.builder(Material.FEATHER, 1)
                .addName(ChatColor.DARK_RED + "Werewolf Dash")
                .addLore(ChatColor.GREEN + "Right click to use")
                .addLore(ChatColor.BLUE + "Gives a big movement boost in a direction")
                .addLore(ChatColor.GRAY + "Can only be obtained in a Werewolf Night")
                .build();
    }

    @Override
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.isSprinting()) {
            player.setVelocity(player.getLocation().getDirection().multiply(2).setY(0.5));
        } else {
            player.setVelocity(player.getLocation().getDirection().multiply(3.5).setY(0.5));
        }
        if (event.getItem() != null) {
            event.getItem().setAmount(event.getItem().getAmount() - 1);
        }
    }
}
