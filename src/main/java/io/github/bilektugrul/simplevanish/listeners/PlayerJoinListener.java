package io.github.bilektugrul.simplevanish.listeners;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.commands.VanishCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import java.util.UUID;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.Listener;

public class PlayerJoinListener implements Listener {

    public PlayerJoinListener(SimpleVanish main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        if (SimpleVanish.plugin.getBoolean("join-quit-messages.enabled", false)) {
            if (!VanishCommand.instance.isVanished(uuid)) {
                if (player.hasPlayedBefore())
                    e.setJoinMessage(SimpleVanish.plugin.getString("join-quit-messages.join-message", player));
                else
                    e.setJoinMessage(SimpleVanish.plugin.getString("join-quit-messages.first-join-message", player));

            } else {
                e.setJoinMessage("");
            }
        }

        if (!player.hasPermission("simplevanish.admin")) {
            for (UUID vanished : VanishCommand.instance.onlineVanishPlayers) {
                player.hidePlayer(SimpleVanish.plugin, Bukkit.getPlayer(vanished));
            }

        } else if (VanishCommand.instance.isVanished(uuid)) VanishCommand.instance.hidePlayer(player, true);

    }
}
