package net.aesten.werewolfrpg.plugin.packets;

import java.util.List;
import java.util.ListIterator;

import net.aesten.werewolfrpg.WerewolfRpg;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.aesten.werewolfrpg.plugin.core.WerewolfGame;
import net.aesten.werewolfrpg.plugin.data.Role;

public class SpecInfoPacket extends PacketAdapter {
    public SpecInfoPacket(WerewolfRpg plugin) {
        super(plugin, PacketType.Play.Server.PLAYER_INFO);
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        try {
            PlayerInfoAction action = e.getPacket().getPlayerInfoAction().read(0);
            if (action != PlayerInfoAction.ADD_PLAYER && action != PlayerInfoAction.UPDATE_GAME_MODE) return;

            String playerName = e.getPlayer().getName();
            PacketContainer packet = e.getPacket().shallowClone();

            List<PlayerInfoData> dataList = packet.getPlayerInfoDataLists().read(0);
            ListIterator<PlayerInfoData> dataListIt = dataList.listIterator();
            while (dataListIt.hasNext()) {
                PlayerInfoData data = dataListIt.next();
                WrappedGameProfile profile = data.getProfile();
                NativeGameMode gameMode = data.getGameMode();
                if (gameMode != NativeGameMode.SPECTATOR || profile.getName().equals(playerName) ||
                        WerewolfGame.getTeamsManager().getFaction(Role.SPECTATOR).getTeam().getEntries().contains(profile.getName())) continue;

                PlayerInfoData newData = new PlayerInfoData(profile, data.getLatency(),
                        NativeGameMode.ADVENTURE, data.getDisplayName());
                dataListIt.set(newData);
            }

            packet.getPlayerInfoDataLists().write(0, dataList);
            e.setPacket(packet);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
