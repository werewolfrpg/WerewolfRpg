package net.aesten.werewolfmc.plugin.data;

import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.items.base.ShopWerewolfItem;
import net.aesten.werewolfmc.plugin.items.base.WerewolfItem;
import net.aesten.werewolfmc.plugin.items.registry.PlayerItem;
import net.azalealibrary.command.Arguments;
import net.azalealibrary.configuration.Configurable;
import net.azalealibrary.configuration.property.*;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class WerewolfConfig implements Configurable {
    //skeleton config
    private static final AssignmentPolicy<Double> POSITIVE_DOUBLE = AssignmentPolicy.create(d -> d > 0, "Negative values are not accepted");
    private static final AssignmentPolicy<Integer> POSITIVE_INTEGER = AssignmentPolicy.create(i -> i > 0, "Negative values are not accepted");
    private static final AssignmentPolicy<Double> PROPORTION = AssignmentPolicy.create(d -> d >= 0 && d <= 1, "Proportions should be between 0 and 1");

    private static final PropertyType<ShopWerewolfItem> WEREWOLF_ITEM = new PropertyType<>(ShopWerewolfItem.class) {
        @Override
        public List<String> complete(CommandSender sender, Arguments arguments) {
            return WerewolfGame.getShopManager().getAllShopItems().stream().map(ShopWerewolfItem::getId).toList();
        }

        @Override
        public ShopWerewolfItem parse(CommandSender sender, Arguments arguments) {
            WerewolfItem item = PlayerItem.getItemFromId(arguments.getLast());
            if (item instanceof ShopWerewolfItem shopItem) return shopItem;
            else return null;
        }

        @Override
        public Object serialize(ShopWerewolfItem object) {
            return object.getId();
        }

        @Override
        public ShopWerewolfItem deserialize(Object object) {
            WerewolfItem item = PlayerItem.getItemFromId((String) object);
            if (item instanceof ShopWerewolfItem shopItem) return shopItem;
            else return null;
        }

        @Override
        public String print(ShopWerewolfItem object) {
            return object.getId();
        }
    };

    private static final List<String> defaultSpecialSkeletonDropTableIds = List.of("ash_of_the_dead", "invisibility_potion", "light_of_revelation", "skeleton_slicer", "stun_grenade", "curse_spear");
    private static final List<ShopWerewolfItem> defaultSpecialSkeletonDropTable = defaultSpecialSkeletonDropTableIds.stream().map(PlayerItem::getItemFromId).filter(item -> item instanceof ShopWerewolfItem).map(ShopWerewolfItem.class::cast).toList();

    private final Property<Integer> basicSkeletonSpawnNumberPerPlayer = Property.create(PropertyType.INTEGER, "skeleton.basic.spawn_number_per_player", () -> 12)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> basicSkeletonDamage = Property.create(PropertyType.DOUBLE, "skeleton.basic.damage", () -> 4.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> basicSkeletonHealth = Property.create(PropertyType.DOUBLE, "skeleton.basic.health", () -> 12.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> basicSkeletonEmeraldDropRate = Property.create(PropertyType.DOUBLE, "skeleton.basic.emerald_drop_rate", () -> 0.5)
            .addPolicy(PROPORTION)
            .done();

    private final Property<Boolean> luckySkeletonEnable = Property.create(PropertyType.BOOLEAN, "skeleton.lucky.enable", () -> true)
            .done();
    private final Property<Integer> luckySkeletonMaxSpawnNumber = Property.create(PropertyType.INTEGER, "skeleton.lucky.max_spawn_number", () -> 4)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> luckySkeletonSpawnChance = Property.create(PropertyType.DOUBLE, "skeleton.lucky.spawn_chance", () -> 0.5)
            .addPolicy(PROPORTION)
            .done();
    private final Property<Double> luckySkeletonDamage = Property.create(PropertyType.DOUBLE, "skeleton.lucky.damage", () -> 3.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> luckySkeletonHealth = Property.create(PropertyType.DOUBLE, "skeleton.lucky.health", () -> 20.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Integer> luckySkeletonEmeraldDropNumber = Property.create(PropertyType.INTEGER, "skeleton.lucky.emerald_drop_number", () -> 2)
            .addPolicy(POSITIVE_INTEGER)
            .done();

    private final Property<Boolean> specialSkeletonEnable = Property.create(PropertyType.BOOLEAN, "skeleton.special.enable", () -> true)
            .done();
    private final Property<Integer> specialSkeletonMaxSpawnNumber = Property.create(PropertyType.INTEGER, "skeleton.special.max_spawn_number", () -> 1)
            .addPolicy(POSITIVE_INTEGER)
            .done();
    private final Property<Double> specialSkeletonSpawnChance = Property.create(PropertyType.DOUBLE, "skeleton.special.spawn_chance", () -> 0.4)
            .addPolicy(PROPORTION)
            .done();
    private final Property<Double> specialSkeletonDamage = Property.create(PropertyType.DOUBLE, "skeleton.special.damage", () -> 5.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final Property<Double> specialSkeletonHealth = Property.create(PropertyType.DOUBLE, "skeleton.special.health", () -> 32.0)
            .addPolicy(POSITIVE_DOUBLE)
            .done();
    private final ListProperty<ShopWerewolfItem> specialSkeletonDrops = ListProperty.create(WEREWOLF_ITEM, "skeleton.special.drop_table", () -> defaultSpecialSkeletonDropTable)
            .done();

    private List<ConfigurableProperty<?, ?>> getSkeletonProperties() {
        return List.of(
                basicSkeletonSpawnNumberPerPlayer,
                basicSkeletonHealth,
                basicSkeletonDamage,
                basicSkeletonEmeraldDropRate,
                luckySkeletonEnable,
                luckySkeletonMaxSpawnNumber,
                luckySkeletonSpawnChance,
                luckySkeletonHealth,
                luckySkeletonDamage,
                luckySkeletonEmeraldDropNumber,
                specialSkeletonEnable,
                specialSkeletonMaxSpawnNumber,
                specialSkeletonSpawnChance,
                specialSkeletonHealth,
                specialSkeletonDamage,
                specialSkeletonDrops
        );
    }


    //score config
    private static final AssignmentPolicy<Integer> POSITIVE = AssignmentPolicy.create(i -> i >= 0, "Score threshold should be positive");

    private final Property<Integer> rank0 = Property.create(PropertyType.INTEGER, "score.rank.beginner", () -> 0).addPolicy(POSITIVE).done();
    private final Property<Integer> rank1 = Property.create(PropertyType.INTEGER, "score.rank.novice", () -> 100).addPolicy(POSITIVE).done();
    private final Property<Integer> rank2 = Property.create(PropertyType.INTEGER, "score.rank.apprentice", () -> 200).addPolicy(POSITIVE).done();
    private final Property<Integer> rank3 = Property.create(PropertyType.INTEGER, "score.rank.intermediate", () -> 300).addPolicy(POSITIVE).done();
    private final Property<Integer> rank4 = Property.create(PropertyType.INTEGER, "score.rank.skilled", () -> 500).addPolicy(POSITIVE).done();
    private final Property<Integer> rank5 = Property.create(PropertyType.INTEGER, "score.rank.experienced", () -> 700).addPolicy(POSITIVE).done();
    private final Property<Integer> rank6 = Property.create(PropertyType.INTEGER, "score.rank.veteran", () -> 1000).addPolicy(POSITIVE).done();
    private final Property<Integer> rank7 = Property.create(PropertyType.INTEGER, "score.rank.expert", () -> 1500).addPolicy(POSITIVE).done();
    private final Property<Integer> rank8 = Property.create(PropertyType.INTEGER, "score.rank.elite", () -> 2000).addPolicy(POSITIVE).done();
    private final Property<Integer> rank9 = Property.create(PropertyType.INTEGER, "score.rank.legendary", () -> 3000).addPolicy(POSITIVE).done();

    private final Property<Integer> thirdPartyVictoryScoreGain = Property.create(PropertyType.INTEGER, "score.gain.victory.third_party", () -> 25).done();
    private final Property<Integer> traitorVictoryScoreGain = Property.create(PropertyType.INTEGER, "score.gain.victory.traitor", () -> 20).done();
    private final Property<Integer> baseVictoryScoreGain = Property.create(PropertyType.INTEGER, "score.gain.victory.base", () -> 15).done();
    private final Property<Integer> thirdPartyDefeatScoreGain = Property.create(PropertyType.INTEGER, "score.gain.defeat.third_party", () -> 10).done();
    private final Property<Integer> baseDefeatScoreGain = Property.create(PropertyType.INTEGER, "score.gain.defeat.base", () -> 5).done();

    private List<ConfigurableProperty<?, ?>> getScoreProperties() {
        return List.of(rank0, rank1, rank2, rank3, rank4, rank5, rank6, rank7, rank8, rank9,
                thirdPartyVictoryScoreGain, traitorVictoryScoreGain, baseVictoryScoreGain, thirdPartyDefeatScoreGain, baseDefeatScoreGain);
    }


    //shop config
    private List<Property<Integer>> getShopProperties() {
        return PlayerItem.getRegistry().values().stream().filter(item -> item instanceof ShopWerewolfItem).map(item -> ((ShopWerewolfItem) item).getCost()).toList();
    }

    //gameplay config
    private final Property<Boolean> enableWerewolfNight = Property.create(PropertyType.BOOLEAN, "gameplay.werewolf_night.enable", () -> true).done();
    private final Property<Integer> firstWerewolfNight = Property.create(PropertyType.INTEGER, "gameplay.werewolf_night.first_night", () -> 3).done();
    private final Property<Double> chanceWerewolfNight = Property.create(PropertyType.DOUBLE, "gameplay.werewolf_night.chance", () -> 0.4).done();
    private final Property<Boolean> enableServant = Property.create(PropertyType.BOOLEAN, "gameplay.servant.enable", () -> false).done();

    private List<ConfigurableProperty<?, ?>> getGameplayProperties() {
        return List.of(enableWerewolfNight, firstWerewolfNight, chanceWerewolfNight, enableServant);
    }

    //config methods
    @Override
    public String getName() {
        return "werewolf-plugin-config";
    }

    @Override
    public List<ConfigurableProperty<?, ?>> getProperties() {
        List<ConfigurableProperty<?, ?>> list = new ArrayList<>();
        Stream.of(getSkeletonProperties(), getShopProperties(), getScoreProperties(), getGameplayProperties()).forEach(list::addAll);
        return list;
    }

    public Property<Integer> getBasicSkeletonSpawnNumberPerPlayer() {
        return basicSkeletonSpawnNumberPerPlayer;
    }

    public Property<Double> getBasicSkeletonDamage() {
        return basicSkeletonDamage;
    }

    public Property<Double> getBasicSkeletonHealth() {
        return basicSkeletonHealth;
    }

    public Property<Double> getBasicSkeletonEmeraldDropRate() {
        return basicSkeletonEmeraldDropRate;
    }

    public Property<Boolean> getLuckySkeletonEnable() {
        return luckySkeletonEnable;
    }

    public Property<Integer> getLuckySkeletonMaxSpawnNumber() {
        return luckySkeletonMaxSpawnNumber;
    }

    public Property<Double> getLuckySkeletonSpawnChance() {
        return luckySkeletonSpawnChance;
    }

    public Property<Double> getLuckySkeletonDamage() {
        return luckySkeletonDamage;
    }

    public Property<Double> getLuckySkeletonHealth() {
        return luckySkeletonHealth;
    }

    public Property<Integer> getLuckySkeletonEmeraldDropNumber() {
        return luckySkeletonEmeraldDropNumber;
    }

    public Property<Boolean> getSpecialSkeletonEnable() {
        return specialSkeletonEnable;
    }

    public Property<Integer> getSpecialSkeletonMaxSpawnNumber() {
        return specialSkeletonMaxSpawnNumber;
    }

    public Property<Double> getSpecialSkeletonSpawnChance() {
        return specialSkeletonSpawnChance;
    }

    public Property<Double> getSpecialSkeletonDamage() {
        return specialSkeletonDamage;
    }

    public Property<Double> getSpecialSkeletonHealth() {
        return specialSkeletonHealth;
    }

    public ListProperty<ShopWerewolfItem> getSpecialSkeletonDrops() {
        return specialSkeletonDrops;
    }

    public Property<Integer> getRank0() {
        return rank0;
    }

    public Property<Integer> getRank1() {
        return rank1;
    }

    public Property<Integer> getRank2() {
        return rank2;
    }

    public Property<Integer> getRank3() {
        return rank3;
    }

    public Property<Integer> getRank4() {
        return rank4;
    }

    public Property<Integer> getRank5() {
        return rank5;
    }

    public Property<Integer> getRank6() {
        return rank6;
    }

    public Property<Integer> getRank7() {
        return rank7;
    }

    public Property<Integer> getRank8() {
        return rank8;
    }

    public Property<Integer> getRank9() {
        return rank9;
    }

    public Property<Integer> getThirdPartyVictoryScoreGain() {
        return thirdPartyVictoryScoreGain;
    }

    public Property<Integer> getTraitorVictoryScoreGain() {
        return traitorVictoryScoreGain;
    }

    public Property<Integer> getBaseVictoryScoreGain() {
        return baseVictoryScoreGain;
    }

    public Property<Integer> getThirdPartyDefeatScoreGain() {
        return thirdPartyDefeatScoreGain;
    }

    public Property<Integer> getBaseDefeatScoreGain() {
        return baseDefeatScoreGain;
    }

    public Property<Boolean> getEnableWerewolfNight() {
        return enableWerewolfNight;
    }

    public Property<Integer> getFirstWerewolfNight() {
        return firstWerewolfNight;
    }

    public Property<Double> getChanceWerewolfNight() {
        return chanceWerewolfNight;
    }

    public Property<Boolean> getEnableServant() {
        return enableServant;
    }
}
