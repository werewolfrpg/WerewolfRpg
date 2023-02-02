package net.aesten.wwrpg.config;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class WerewolfConfig {
    private ShopConfig shopConfig;
    private SkeletonConfig skeletonConfig;
    private TeamCompositionConfig teamCompositionConfig;

    public static class ShopConfig {
        @JsonProperty("prices")
        private Map<String, Integer> shopPrices;

        public Map<String, Integer> getShopPrices() {
            return shopPrices;
        }

        public void setShopPrices(Map<String, Integer> shopPrices) {
            this.shopPrices = shopPrices;
        }
    }


    public static class SkeletonConfig {
        @JsonProperty("spawns")
        private Integer spawnParameter;

        @JsonProperty("damage")
        private Double skeletonDamage;

        @JsonProperty("health")
        private Double skeletonHealth;
    }

    public static class TeamCompositionConfig {
        private Map<Integer, List<Integer>> teamCompositions;


    }
}
