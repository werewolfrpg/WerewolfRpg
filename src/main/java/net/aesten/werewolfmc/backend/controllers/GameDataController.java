package net.aesten.werewolfmc.backend.controllers;

import com.google.gson.annotations.SerializedName;
import io.javalin.http.Context;
import net.aesten.werewolfmc.plugin.core.WerewolfGame;
import net.aesten.werewolfmc.plugin.map.WerewolfMap;

import java.util.List;

public class GameDataController {
    public void apiGetMaps(Context ctx) {
        List<MapInfo> maps = WerewolfGame.getMapManager().getMaps().values().stream().filter(map -> !map.getName().equals("lobby")).map(MapInfo::new).toList();
        ctx.json(maps);
    }

    private static final class MapInfo {
        @SerializedName("name")
        private final String mapName;
        @SerializedName("image")
        private final String image;

        public MapInfo(WerewolfMap map) {
            this.mapName = map.getName();
            this.image = map.getImage();
        }
    }
}
