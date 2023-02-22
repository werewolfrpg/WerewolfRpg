package net.aesten.wwrpg.tracker;

import net.aesten.wwrpg.data.Role;

import java.util.UUID;

public class PlayerStats {
    private UUID PlayerId;
    private Result result; //win rate = W / (W+L+D)
    private Role role;

    //basic stats
    private int kills = 0; //kills by role
    private int deaths = 0; //deaths by role + stupid death causes?
    private int killedBasicSkeletons = 0;
    private int killedLuckySkeletons = 0;
    private int killedSpecialSkeletons = 0;
    private int gainedEmeralds = 0;

    ////item stats
    private int steaksEaten = 0;
    private int asheUsed = 0;
    private int divinationUsed = 0;
    private int invisibilityUsed = 0;
    private int swiftnessUsed = 0;
    private int revelationUsed = 0;
    private int traitorsGuideUsed = 0;

    //effectiveness
    private ItemUses curseSpearMelee = new ItemUses();
    private ItemUses curseSpearThrow = new ItemUses();
    private ItemUses arrow = new ItemUses();
    private ItemUses stunGrenade = new ItemUses();
    private ItemUses holyStar = new ItemUses();
    private ItemUses protection = new ItemUses();
    private ItemUses sneakNotice = new ItemUses();
    private ItemUses werewolfAxe = new ItemUses();



}
