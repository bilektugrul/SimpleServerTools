package io.github.bilektugrul.simpleservertools.features.vanish;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class VanishManager {

    private final Set<UUID> vanishedPlayers = new HashSet<>();
    private final Set<UUID> onlineVanishedPlayers = new HashSet<>();

    public Set<UUID> getVanishedPlayers() {
        return new HashSet<>(vanishedPlayers);
    }

    public Set<UUID> getOnlineVanishedPlayers() {
        return new HashSet<>(onlineVanishedPlayers);
    }

    public void removeOnlineVanishedPlayer(UUID uuid) {
        onlineVanishedPlayers.remove(uuid);
    }

    public void removeVanishedPlayer(UUID uuid) {
        vanishedPlayers.remove(uuid);
    }

    public void hidePlayer(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        vanishedPlayers.add(uuid);
        onlineVanishedPlayers.add(uuid);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission("sst.vanish")) {
                p.hidePlayer(player);
            }
        }
        player.sendMessage(Utils.getMessage("vanish.activated", player));
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(Utils.getString("join-quit-messages.quit-message", player));
        }
    }

    public void showPlayer(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.canSee(player)) { // To avoid more packets
                p.showPlayer(player);
            }
        }
        player.sendMessage(Utils.getMessage("vanish.disabled", player));
        vanishedPlayers.remove(uuid);
        onlineVanishedPlayers.remove(uuid);
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(Utils.getString("join-quit-messages.join-message", player));
        }
    }

    public void toggleVanish(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        if (isVanished(uuid)) showPlayer(player, silent);
        else hidePlayer(player, silent);
    }

    public String modeString(Player player) {
        boolean mode = isVanished(player.getUniqueId());
        return Utils.getMessage("vanish.modes." + mode);
    }

    public boolean isVanished(UUID uuid) {
        return vanishedPlayers.contains(uuid);
    }

}