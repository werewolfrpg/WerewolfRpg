package net.aesten.werewolfrpg.plugin.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RolePool {
    private int werewolfNumber;
    private int traitorNumber;
    private int vampireNumber;
    private int possessedNumber;

    public RolePool(int werewolfNumber, int traitorNumber, int vampireNumber, int possessedNumber) {
        this.werewolfNumber = werewolfNumber;
        this.traitorNumber = traitorNumber;
        this.vampireNumber = vampireNumber;
        this.possessedNumber = possessedNumber;
    }

    private List<Role> getPonderedSpecialRoles() {
        List<Role> list = new ArrayList<>();
        list.addAll(Collections.nCopies(werewolfNumber, Role.WEREWOLF));
        list.addAll(Collections.nCopies(traitorNumber, Role.TRAITOR));
        list.addAll(Collections.nCopies(vampireNumber, Role.VAMPIRE));
        list.addAll(Collections.nCopies(possessedNumber, Role.POSSESSED));
        return list;
    }

    public List<Role> getRoles(int participants) {
        List<Role> list = getPonderedSpecialRoles();
        list.addAll(Collections.nCopies(participants - list.size(), Role.VILLAGER));
        Collections.shuffle(list);
        return list;
    }

    public int getTotalSpecialRoles() {
        return werewolfNumber + traitorNumber + vampireNumber + possessedNumber;
    }

    public int getWerewolfNumber() {
        return werewolfNumber;
    }

    public void setWerewolfNumber(int werewolfNumber) {
        this.werewolfNumber = werewolfNumber;
    }

    public int getTraitorNumber() {
        return traitorNumber;
    }

    public void setTraitorNumber(int traitorNumber) {
        this.traitorNumber = traitorNumber;
    }

    public int getVampireNumber() {
        return vampireNumber;
    }

    public void setVampireNumber(int vampireNumber) {
        this.vampireNumber = vampireNumber;
    }

    public int getPossessedNumber() {
        return possessedNumber;
    }

    public void setPossessedNumber(int possessedNumber) {
        this.possessedNumber = possessedNumber;
    }
}
