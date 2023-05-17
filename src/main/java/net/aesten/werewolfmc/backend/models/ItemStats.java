package net.aesten.werewolfmc.backend.models;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "item_stats")
public class ItemStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "item_name", nullable = false)
    @SerializedName("name")
    private String itemName;
    @ElementCollection
    @CollectionTable(name = "item_stats_kv", joinColumns = @JoinColumn(name = "item_stats_id"))
    @MapKeyColumn(name = "key_name")
    @Column(name = "value")
    @SerializedName("stats")
    private Map<String, Integer> stats;

    public ItemStats(String itemName, Map<String, Integer> stats) {
        this.itemName = itemName;
        this.stats = stats;
    }

    public ItemStats(String itemName) {
        this(itemName, new HashMap<>());
    }

    public ItemStats() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }
}
