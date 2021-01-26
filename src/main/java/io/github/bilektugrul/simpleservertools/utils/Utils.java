package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Utils {

    private static final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);

    public static void hidePlayer(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        if (!isVanished(uuid))
            plugin.getVanishedPlayers().add(player.getUniqueId());
        if (!plugin.getOnlineVanishedPlayers().contains(uuid))
            plugin.getOnlineVanishedPlayers().add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.hasPermission(SimpleServerTools.vanishPerm)) {
                p.hidePlayer(player);
            }
        }
        player.sendMessage(getString("other-messages.vanish.activated", player));
        if (getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(getString("join-quit-messages.quit-message", player));
        }
    }

    public static void showPlayer(Player player, boolean silent) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.showPlayer(player);
        }
        player.sendMessage(getString("other-messages.vanish.disabled", player));
        plugin.getVanishedPlayers().remove(player.getUniqueId());
        plugin.getOnlineVanishedPlayers().remove(player.getUniqueId());
        if (getBoolean("join-quit-messages.enabled", false)) {
            if (!silent) Bukkit.broadcastMessage(getString("join-quit-messages.join-message", player));
        }
    }

    public static boolean isVanished(UUID uuid) {
        return plugin.getVanishedPlayers().contains(uuid);
    }

    public static String getString(String string, Player player) {
        return colorMessage(PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString(string))
                .replace("%player%", player.getName()));
    }

    public static String getString(String string) {
        return colorMessage(PlaceholderAPI.setPlaceholders(null, plugin.getConfig().getString(string)));
    }

    public static String getPAPILessString(String string) {
        return colorMessage(plugin.getConfig().getString(string));
    }

    public static String colorMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg)
                .replace("%prefix%", SimpleServerTools.prefix)
                .replace("%prefix:warp%", SimpleServerTools.warpPrefix);
    }

    public static boolean getBoolean(String string, boolean def) {
        return plugin.getConfig().getBoolean(string, def);
    }

    public static boolean getBoolean(String string) {
        return plugin.getConfig().getBoolean(string);
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

}
