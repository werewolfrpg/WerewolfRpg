package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
import io.javalin.http.Context;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;
import net.aesten.werewolfmc.plugin.statistics.Rank;

import java.util.*;

public class GameDataController {
    public void apiGetMaps(Context ctx) {
        List<MapInfo> maps = WerewolfGame.getMapManager().getMaps().values().stream().filter(map -> !map.getName().equals("lobby")).map(MapInfo::new).toList();
        ctx.json(maps);
    }

    private static final class MapInfo {
        @SerializedName("name")
        private final String mapName;
        @SerializedName("description")
        private final String description;
        @SerializedName("image")
        private final String image;
        @SerializedName("tags")
        private final List<String> tags;

        public MapInfo(WerewolfMap map) {
            this.mapName = map.getName();
            this.description = map.getDescription();
            this.image = map.getImage();
            this.tags = map.getTags();
        }
    }

    public void apiGetRolesFactions(Context ctx) {
        Map<Faction, FactionRoleInfo> infoMap = new LinkedHashMap<>();

        Arrays.stream(Faction.values()).forEach(faction -> infoMap.put(faction, new FactionRoleInfo(faction)));
        Arrays.stream(Role.values()).forEach(role -> infoMap.get(role.getFaction()).roles.put(role, new RoleInfo(role)));
        ctx.json(infoMap);
    }

    private static final class FactionRoleInfo {
        @SerializedName("name")
        private final String factionName;
        @SerializedName("color")
        private final String factionColor;
        @SerializedName("description")
        private final String factionDescription;
        @SerializedName("roles")
        private final Map<Role, RoleInfo> roles;

        public FactionRoleInfo(Faction faction) {
            this.factionName = faction.getName();
            this.factionColor = faction.getColorCode();
            this.factionDescription = faction.getDescription();
            this.roles = new LinkedHashMap<>();
        }
    }

    private static final class RoleInfo {
        @SerializedName("name")
        private final String roleName;
        @SerializedName("color")
        private final String roleColor;
        @SerializedName("description")
        private final String roleDescription;

        public RoleInfo(Role role) {
            this.roleName = role.getName();
            this.roleColor = role.getColorCode();
            this.roleDescription = role.getDescription();
        }
    }

    public void apiGetRanks(Context ctx) {
        ctx.json(Arrays.stream(Rank.values()).map(RankInfo::new).toList());
    }

    private static final class RankInfo {
        @SerializedName("rank")
        private final Rank rank;
        @SerializedName("color")
        private final String color;
        @SerializedName("threshold")
        private final int threshold;

        public RankInfo(Rank rank) {
            this.rank = rank;
            this.color = rank.getColorHex();
            this.threshold = WerewolfGame.getScoreManager().getScoreThresholdOfRank(rank);
        }
    }
}
