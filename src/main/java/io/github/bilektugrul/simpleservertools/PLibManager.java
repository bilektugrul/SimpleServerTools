package io.github.bilektugrul.simpleservertools;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.github.bilektugrul.simpleservertools.listeners.PacketListener;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;

public class PLibManager {

    private static final ProtocolManager manager = ProtocolLibrary.getProtocolManager();

    public static void loadPacketListener(SimpleServerTools plugin) {
        if ((Utils.getBoolean("remove-vanished-players.enabled", false)) || (Utils.getBoolean("one-more-slot.enabled", false))) {
            if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
                manager.removePacketListeners(plugin);
                manager.addPacketListener(new PacketListener(plugin));
            } else {
                plugin.getLogger().warning("SimpleVanish requires ProtocolLib for removing players from client player " +
                        "list and changing max player - player count but you don't have ProtocolLib.");
            }
        }
    }


}
