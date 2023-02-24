package net.aesten.wwrpg.tracker;

public class ItemUses {
    private int uses = 0;
    private int successes = 0;

    public int getUses() {
        return uses;
    }

    public int getSuccesses() {
        return successes;
    }

    public void addUse(boolean success) {
        this.uses += 1;
        if (success) this.successes += 1;
    }
}
