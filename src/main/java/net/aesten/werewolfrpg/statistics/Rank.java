package net.aesten.werewolfrpg.statistics;

public enum Rank {
    BEGINNER("[Beginner]"),
    NOVICE("[Novice]"),
    APPRENTICE("[Apprentice]"),
    INTERMEDIATE("[Intermediate]"),
    SKILLED("[Skilled]"),
    EXPERIENCED("[Experienced]"),
    VETERAN("[Veteran]"),
    EXPERT("[Expert]"),
    ELITE("[Elite]"),
    LEGENDARY("[Legendary]");

    private final String prefix;

    Rank(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}
