package io.github.bilektugrul.simpleservertools.features.vanish;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class VanishManager {

    private final List<UUID> vanishedPlayers = new ArrayList<>();
    private final List<UUID> onlineVanishedPlayers = new ArrayList<>();

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<UUID> getOnlineVanishedPlayers() {
        return onlineVanishedPlayers;
    }

    public void hidePlayer(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        if (!isVanished(uuid))
            vanishedPlayers.add(player.getUniqueId());
        if (!getOnlineVanishedPlayers().contains(uuid))
            getOnlineVanishedPlayers().add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("sst.vanish")) {
                p.hidePlayer(player);
            }
        }
        player.sendMessage(Utils.getString("other-messages.vanish.activated", player));
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(Utils.getString("join-quit-messages.quit-message", player));
        }
    }

    public void showPlayer(Player player, boolean silent) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
        player.sendMessage(Utils.getString("other-messages.vanish.disabled", player));
        getVanishedPlayers().remove(player.getUniqueId());
        getOnlineVanishedPlayers().remove(player.getUniqueId());
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(Utils.getString("join-quit-messages.join-message", player));
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

}
