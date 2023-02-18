package net.aesten.wwrpg.items.registry;

import net.aesten.wwrpg.items.models.WerewolfItem;
import net.aesten.wwrpg.items.registry.player.*;

import java.util.HashMap;
import java.util.Map;

public enum Item {
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
    CURSE_SPEAR(new CurseSpear());

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
}
