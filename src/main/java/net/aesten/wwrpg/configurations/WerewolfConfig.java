package net.aesten.wwrpg.configurations;

import java.util.List;
import java.util.Map;

public class WerewolfConfig {
    private ShopConfig shopConfig;
    private SkeletonConfig skeletonConfig;
    private TeamCompositionConfig teamCompositionConfig;

    public static class ShopConfig {
        private Map<String, Integer> shopPrices;
        public Map<String, Integer> getShopPrices() {
            return shopPrices;
        }
        public void setShopPrices(Map<String, Integer> shopPrices) {
            this.shopPrices = shopPrices;
        }
    }


    public static class SkeletonConfig {
        private Integer spawnParameter;
        private Double skeletonDamage;
        private Double skeletonHealth;
    }

    public static class TeamCompositionConfig {
        private Map<Integer, List<Integer>> teamCompositions;


    }
}
