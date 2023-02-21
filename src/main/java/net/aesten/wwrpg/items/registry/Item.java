package net.aesten.wwrpg.items.registry;

import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.items.registry.admin.BasicVillagerSummonWand;
import net.aesten.wwrpg.items.registry.admin.SkeletonSpawnPointWand;
import net.aesten.wwrpg.items.registry.admin.SpecialVillagerSummonWand;
import net.aesten.wwrpg.items.registry.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public enum Item {
    //Game items
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

    //Admin items
    BASIC_VILLAGER_SUMMON_WAND(new BasicVillagerSummonWand()),
    SPECIAL_VILLAGER_SUMMON_WAND(new SpecialVillagerSummonWand()),
    SKELETON_SPAWN_POINT_WAND(new SkeletonSpawnPointWand());

    private static final Map<String, WerewolfItem> registry = new HashMap<>();

    static {
        for (Item item : values()) {
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

    Item(WerewolfItem werewolfItem) {
        this.id = werewolfItem.getId();
        this.werewolfItem = werewolfItem;
    }

    public ItemStack getItem() {
        return werewolfItem.getItem();
    }
}
