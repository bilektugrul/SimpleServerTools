package io.github.bilektugrul.simplevanish.listeners;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import org.bukkit.event.EventHandler;
import java.util.UUID;
import io.github.bilektugrul.simplevanish.commands.VanishCommand;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;

public class PlayerQuitListener implements Listener {

    public PlayerQuitListener(SimpleVanish main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        UUID uuid = e.getPlayer().getUniqueId();

        if (SimpleVanish.plugin.getBoolean("join-quit-messages.enabled", false)) {
            if (!VanishCommand.instance.isVanished(uuid)) e.setQuitMessage(SimpleVanish.plugin.getString("join-quit-messages.quit-message", e.getPlayer()));
        }

        if (VanishCommand.instance.isVanished(uuid)) VanishCommand.instance.onlineVanishPlayers.remove(uuid);

    }
}
