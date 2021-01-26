package io.github.bilektugrul.simpleservertools.listeners;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PlayerListener implements Listener {

    private SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!Utils.isVanished(uuid)) {
                if (player.hasPlayedBefore())
                    e.setJoinMessage(Utils.getString("join-quit-messages.join-message", player));
                else
                    e.setJoinMessage(Utils.getString("join-quit-messages.first-join-message", player));

            } else {
                e.setJoinMessage("");
            }
        }

        if (!player.hasPermission("simplevanish.admin")) {
            for (UUID vanished : plugin.getOnlineVanishedPlayers()) {
                player.hidePlayer(Bukkit.getPlayer(vanished));
            }

        } else if (Utils.isVanished(uuid)) Utils.hidePlayer(player, true);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        UUID uuid = e.getPlayer().getUniqueId();

        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!Utils.isVanished(uuid)) e.setQuitMessage(Utils.getString("join-quit-messages.quit-message", e.getPlayer()));
        }

        if (Utils.isVanished(uuid)) plugin.getOnlineVanishedPlayers().remove(uuid);

    }

}
