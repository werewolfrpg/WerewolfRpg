package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
import io.javalin.http.Context;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.data.Faction;
import net.aesten.werewolfmc.plugin.data.Role;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;

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
        Map<Faction, FactionRoleInfo> infoMap = new HashMap<>();
        Arrays.stream(Faction.values()).forEach(faction -> infoMap.put(faction, new FactionRoleInfo(faction)));
        Arrays.stream(Role.values()).forEach(role -> infoMap.get(role.getFaction()).roles.put(role, new RoleInfo(role)));
        ctx.json(infoMap);
    }

    private static final class FactionRoleInfo {
        @SerializedName("factionName")
        private final String factionName;
        @SerializedName("factionColor")
        private final String factionColor;
        @SerializedName("factionRoles")
        private final Map<Role, RoleInfo> roles;

        public FactionRoleInfo(Faction faction) {
            this.factionName = faction.getName();
            this.factionColor = faction.getColorCode();
            this.roles = new HashMap<>();
        }
    }

    private static final class RoleInfo {
        @SerializedName("roleName")
        private final String roleName;
        @SerializedName("roleColor")
        private final String roleColor;

        public RoleInfo(Role role) {
            this.roleName = role.getName();
            this.roleColor = role.getColorCode();
        }
    }
}
