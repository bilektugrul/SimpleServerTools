package io.github.bilektugrul.simplevanish.listeners;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.utils.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoinListener implements Listener {

    private SimpleVanish plugin = JavaPlugin.getPlugin(SimpleVanish.class);

    public PlayerJoinListener(SimpleVanish main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

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
}
