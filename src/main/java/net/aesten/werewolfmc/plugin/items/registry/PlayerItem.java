package net.aesten.werewolfmc.plugin.items.registry;

import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.items.registry.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum PlayerItem {
    MUTER(new Muter()),
    WEREWOLF_DASH(new WerewolfDash()),
    WEREWOLF_TRAP(new WerewolfTrap()),
    SKELETON_PUNISHER(new SkeletonPunisher()),
    EXQUISITE_MEAT(new ExquisiteMeat()),
    HUNTERS_BOW(new HuntersBow()),
    SHARP_ARROW(new SharpArrow()),
    STUN_GRENADE(new StunGrenade()),
    WEREWOLF_AXE(new WerewolfAxe()),
    SKELETON_SLICER(new SkeletonSlicer()),
    INVISIBILITY_POTION(new InvisibilityPotion()),
    SWIFTNESS_POTION(new SwiftnessPotion()),
    LIGHT_OF_REVELATION(new LightOfRevelation()),
    PROTECTION(new Protection()),
    DIVINATION(new Divination()),
    TRAITORS_GUIDE(new TraitorsGuide()),
    HOLY_STAR(new HolyStar()),
    SNEAK_NOTICE(new SneakNotice()),
    ASH_OF_THE_DEAD(new AshOfTheDead()),
    CURSE_SPEAR(new CurseSpear()),
    VAMPIRE_FANG(new VampireFang());

    private static final Map<String, WerewolfItem> registry = new HashMap<>();

    static {
        for (PlayerItem item : values()) {
            registry.put(item.id, item.werewolfItem);
        }
    }

    public static Map<String, WerewolfItem> getRegistry() {
        return registry;
    }

    public static WerewolfItem getItemFromId(String id) {
        return registry.get(id);
    }

    public final String id;
    public final WerewolfItem werewolfItem;

    PlayerItem(WerewolfItem werewolfItem) {
        this.id = werewolfItem.getId();
        this.werewolfItem = werewolfItem;
    }

    public ItemStack getItem() {
        return werewolfItem.getItem();
    }
}
