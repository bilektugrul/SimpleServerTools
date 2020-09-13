package io.github.bilektugrul.simplevanish.listeners;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.utils.Utils;
import org.bukkit.event.EventHandler;
import java.util.UUID;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerQuitListener implements Listener {

    private SimpleVanish plugin = JavaPlugin.getPlugin(SimpleVanish.class);

    public PlayerQuitListener(SimpleVanish main) {
        main.getServer().getPluginManager().registerEvents(this, main);
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
