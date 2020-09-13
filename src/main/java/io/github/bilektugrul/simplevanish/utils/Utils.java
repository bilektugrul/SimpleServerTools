package io.github.bilektugrul.simplevanish.utils;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Utils {

    private static SimpleVanish plugin = JavaPlugin.getPlugin(SimpleVanish.class);

    static String permission = getString("vanish-command-permission");

    public static void hidePlayer(Player player, boolean silent) {
        UUID uuid = player.getUniqueId();
        if (!isVanished(uuid))
            plugin.getVanishedPlayers().add(player.getUniqueId());
        if (!plugin.getOnlineVanishedPlayers().contains(uuid))
            plugin.getOnlineVanishedPlayers().add(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) { if (!p.hasPermission(permission)) { p.hidePlayer(player); } }
        player.sendMessage(getString("vanish-activated", player));
        if (getBoolean("join-quit-messages.enabled", false))
            if (!silent)
                Bukkit.broadcastMessage(getString("join-quit-messages.quit-message", player));
    }

    public static void showPlayer(Player player, boolean silent) {
        for (Player p : Bukkit.getOnlinePlayers()) { p.showPlayer(player); }
        player.sendMessage(getString("vanish-disabled", player));
        plugin.getVanishedPlayers().remove(player.getUniqueId());
        plugin.getOnlineVanishedPlayers().remove(player.getUniqueId());
        if (getBoolean("join-quit-messages.enabled", false))
            if (!silent)
                Bukkit.broadcastMessage(getString("join-quit-messages.join-message", player));
    }

    public static boolean isVanished(UUID uuid) {
        return plugin.getVanishedPlayers().contains(uuid);
    }

    public static String getString(String string, Player player) {
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(player, plugin.getConfig().getString(string))
                .replace("%prefix%", plugin.getConfig().getString("prefix"))
                .replace("%player%", player.getName()));
    }

    public static String getString(String string) {
        return ChatColor.translateAlternateColorCodes('&', PlaceholderAPI.setPlaceholders(null, plugin.getConfig().getString(string))
                .replace("%prefix%", plugin.getConfig().getString("prefix")));
    }

    public static boolean getBoolean(String string, boolean def) {
        return plugin.getConfig().getBoolean(string, def);
    }

}
