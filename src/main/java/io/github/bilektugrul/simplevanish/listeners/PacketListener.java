package io.github.bilektugrul.simplevanish.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.utils.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PacketListener {

    public PacketListener(SimpleVanish main) {
        if (main.getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
            manager.removePacketListeners(main);
            if ((Utils.getBoolean("remove-vanished-players.enabled", false)) || (Utils.getBoolean("one-more-slot.enabled", false))) {
                main.getServer().getScheduler().runTaskAsynchronously(main, () ->
                        manager.addPacketListener(new PacketAdapter(main, ListenerPriority.LOWEST, PacketType.Status.Server.SERVER_INFO) {
                            public void onPacketSending(PacketEvent event) {
                                WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                                Collection<UUID> vanishedPlayers = main.getOnlineVanishedPlayers();
                                int size = main.getServer().getOnlinePlayers().size();
                                int vanishedSize = vanishedPlayers.size();
                                if (Utils.getBoolean("remove-vanished-players.enabled", false)) {
                                    List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(ping.getPlayers());
                                    wrappedGameProfiles.removeIf(wrappedGameProfile -> vanishedPlayers.contains(wrappedGameProfile.getUUID()));
                                    ping.setPlayers(wrappedGameProfiles);
                                    ping.setPlayersOnline(size - vanishedSize);
                                }
                                if (Utils.getBoolean("one-more-slot.enabled", false)) {
                                    if (Utils.getBoolean("remove-vanished-players.enabled", false))
                                        ping.setPlayersMaximum((size - vanishedSize) + 1);
                                    else
                                        ping.setPlayersMaximum(size + 1);
                                }
                                ping.setMotD(Utils.getString("MOTD.value"));
                            }
                        }));
            }
        } else if (Utils.getBoolean("remove-vanished-players.true", false) || Utils.getBoolean("one-more-slot.enabled", false)) {
            main.getLogger().warning("SimpleVanish requires ProtocolLib for removing players from client player list and changing max player - player count but you don't have ProtocolLib.");
        }
    }

}
