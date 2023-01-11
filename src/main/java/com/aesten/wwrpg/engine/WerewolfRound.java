package com.aesten.wwrpg.engine;

import com.aesten.wwrpg.WerewolfUtil;
import org.bukkit.World;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WerewolfRound {
    private List<WerewolfPlayer> participants;
    private Map<Role, List<WerewolfPlayer>> teamsMap;
    private World playingWorld;
    private Ticker ticker;
    private boolean isNight;

    public WerewolfRound(List<WerewolfPlayer> participants) {
        this.participants = participants;
        this.teamsMap = new HashMap<>();

    }

    public Map<Role, List<WerewolfPlayer>> getTeamsMap() {
        return teamsMap;
    }

    public void setTeamsMap(Map<Role, List<WerewolfPlayer>> teamsMap) {
        this.teamsMap = teamsMap;
    }

    public Ticker getTicker() {
        return ticker;
    }

    public void setTicker(Ticker ticker) {
        this.ticker = ticker;
    }

    public boolean isNight() {
        return isNight;
    }

    private final Runnable dayNightSwitcher = () -> {
        if (isNight) {
            ticker.setDayBar();
            //stop day music
            //play night music
            playingWorld.setTime(6000);
            isNight = !isNight;
            teamsMap.values().forEach(
                    l -> l.forEach(
                            wp -> WerewolfUtil.sendPluginText(wp.getPlayer(),
                                    "")
                    )
            );
        }
    }
}
