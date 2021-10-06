package io.github.bilektugrul.simpleservertools.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PacketListener extends PacketAdapter {

    private final SST plugin;
    private final VanishManager vanishManager;

    public PacketListener(SST plugin) {
        super(plugin, ListenerPriority.LOWEST, PacketType.Status.Server.SERVER_INFO);
        this.plugin = plugin;
        this.vanishManager = plugin.getVanishManager();
        this.plugin.getLogger().info(ChatColor.GREEN + "PacketListener enabled!");
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        WrappedServerPing ping = event.getPacket().getServerPings().read(0);
        Collection<UUID> vanishedPlayers = vanishManager.getOnlineVanishedPlayers();

        int size = plugin.getServer().getOnlinePlayers().size();
        int vanishedSize = vanishedPlayers.size();
        boolean removeVanished = Utils.getBoolean("vanish.remove-vanished-players");

        if (removeVanished) {
            List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(ping.getPlayers());
            wrappedGameProfiles.removeIf(wrappedGameProfile -> vanishedPlayers.contains(wrappedGameProfile.getUUID()));
            ping.setPlayers(wrappedGameProfiles);
            ping.setPlayersOnline(size - vanishedSize);
        }

        if (Utils.getBoolean("one-more-slot.enabled")) {
            if (removeVanished)
                ping.setPlayersMaximum((size - vanishedSize) + 1);
            else
                ping.setPlayersMaximum(size + 1);
        }

        if (Utils.getBoolean("motd.enabled")) {
            ping.setMotD(Utils.getString("MOTD.value", null));
        }

    }

}