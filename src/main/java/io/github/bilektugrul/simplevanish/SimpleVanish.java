package io.github.bilektugrul.simplevanish;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import io.github.bilektugrul.simplevanish.commands.VanishCommand;
import io.github.bilektugrul.simplevanish.listeners.PlayerJoinListener;
import io.github.bilektugrul.simplevanish.listeners.PlayerQuitListener;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class SimpleVanish extends JavaPlugin {

    public static SimpleVanish plugin;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        plugin = this;
        getLogger().info("SimpleVanish " + getDescription().getVersion() + " activated!");
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new VanishCommand(this);
        if (Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            if (getBoolean("remove-vanished-players.true", false)) {
                ProtocolManager manager = ProtocolLibrary.getProtocolManager();
                getServer().getScheduler().runTaskAsynchronously(this, () ->
                        manager.addPacketListener(new PacketAdapter(this, ListenerPriority.LOWEST, PacketType.Status.Server.SERVER_INFO) {
                            public void onPacketSending(PacketEvent event) {
                                WrappedServerPing ping = event.getPacket().getServerPings().read(0);
                                Collection<UUID> vanishedPlayers = VanishCommand.instance.vanishPlayers;
                                List<WrappedGameProfile> wrappedGameProfiles = new ArrayList<>(ping.getPlayers());
                                int size = Bukkit.getOnlinePlayers().size();
                                int vanishedSize = vanishedPlayers.size();
                                if (getBoolean("one-more-slot.enabled", false))
                                    ping.setPlayersMaximum((size - vanishedSize) + 1);
                                else
                                    ping.setPlayersMaximum((Bukkit.getMaxPlayers() - vanishedSize));
                                ping.setPlayersOnline(size - vanishedSize);
                                wrappedGameProfiles.removeIf(wrappedGameProfile -> vanishedPlayers.contains(wrappedGameProfile.getUUID()));
                                ping.setPlayers(wrappedGameProfiles);
                            }
                        }));
            }
        } else if (getBoolean("remove-vanished-players.true", false)) {
            getLogger().info("SimpleVanish requires ProtocolLib for removing players from client player list but you don't have ProtocolLib.");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleVanish " + getDescription().getVersion() + " disabled!");
    }

    public String getString(String string, Player player) {
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, this.getConfig().getString(string))
                .replace("%prefix%", getConfig().getString("prefix")));
    }

    public String getString(String string) {
        return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(string))
                .replace("%prefix%", getConfig().getString("prefix"));
    }

    public boolean getBoolean(String string, boolean def) {
        return getConfig().getBoolean(string, def);
    }

}
