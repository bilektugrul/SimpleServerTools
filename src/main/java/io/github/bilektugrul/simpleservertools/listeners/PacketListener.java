package io.github.bilektugrul.simpleservertools.listeners;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PacketListener extends PacketAdapter {

    private final SimpleServerTools plugin;

    public PacketListener(SimpleServerTools plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrappedServerPing ping = event.getPacket().getServerPings().read(0);
        Collection<UUID> vanishedPlayers = plugin.getVanishedPlayers();
        int size = plugin.getServer().getOnlinePlayers().size();
        int vanishedSize = vanishedPlayers.size();
        if (Utils.getBoolean("remove-vanished-players.enabled")) {
            List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(ping.getPlayers());
            wrappedGameProfiles.removeIf(wrappedGameProfile -> vanishedPlayers.contains(wrappedGameProfile.getUUID()));
            ping.setPlayers(wrappedGameProfiles);
            ping.setPlayersOnline(size - vanishedSize);
        }
        if (Utils.getBoolean("one-more-slot.enabled")) {
            if (Utils.getBoolean("remove-vanished-players.enabled"))
                ping.setPlayersMaximum((size - vanishedSize) + 1);
            else
                ping.setPlayersMaximum(size + 1);
        }
        ping.setMotD(Utils.getString("MOTD.value"));
    }

}