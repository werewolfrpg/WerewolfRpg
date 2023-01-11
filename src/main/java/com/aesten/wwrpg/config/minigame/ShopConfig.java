package com.aesten.wwrpg.config.minigame;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class ShopConfig {
    @JsonProperty("prices")
    private Map<String, Integer> shopPrices;

    public Map<String, Integer> getShopPrices() {
        return shopPrices;
    }

    public void setShopPrices(Map<String, Integer> shopPrices) {
        this.shopPrices = shopPrices;
    }
}
