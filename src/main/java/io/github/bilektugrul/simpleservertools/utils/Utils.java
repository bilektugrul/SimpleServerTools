package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.custom.CustomPlaceholderManager;
import me.clip.placeholderapi.PlaceholderAPI;
import me.despical.commons.compat.Titles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class Utils {

    private static final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);
    private static final CustomPlaceholderManager placeholderManager = plugin.getPlaceholderManager();

    public static String getString(String string, CommandSender from) {
        return replacePlaceholders(plugin.getConfig().getString(string) , from);
    }

    public static String replacePlaceholders(String msg, CommandSender from) {
        boolean isPlayer = from instanceof Player;
        msg = placeholderManager.replacePlaceholders(ChatColor.translateAlternateColorCodes('&', msg))
                .replace("%player%", matchName(from));
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(isPlayer ? (Player) from : null, msg);
        }
        return msg;
    }

    public static String matchName(CommandSender entity) {
        boolean isPlayer = entity instanceof Player;
        return isPlayer ? entity.getName() : "CONSOLE";

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

    public static boolean isSameLoc(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX()) && (loc1.getBlockY() == loc2.getBlockY()) && (loc1.getBlockZ() == loc2.getBlockZ());
    }


    public static void sendMessage(Player p, String mode, String msg, String subtitle, String time) {
        msg = msg.replace("%time%", time);
        subtitle = subtitle.replace("%time%", time) ;
        switch (mode) {
            case "CHAT":
                p.sendMessage(msg);
                break;
            case "TITLE":
                Titles.sendTitle(p, msg, subtitle);
                break;
            case "ACTIONBAR":
                ActionBar.sendActionBar(p, msg.replace('\n', ' '));
                break;
        }
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(getString("other-messages." + msg + ".beginning", sender));
        if (sender.hasPermission("sst.staff")) {
            sender.sendMessage(getString("other-messages." + msg + ".only-staff", sender));
        }
        sender.sendMessage(getString("other-messages." + msg + ".everyone", sender));
        sender.sendMessage(getString("other-messages." + msg + ".ending", sender));
    }

    public static boolean matchMode(String mode) {
        mode = mode.toLowerCase(Locale.ROOT);
        if (mode.contains("on") || mode.contains("true") || mode.contains("aç") || mode.contains("aktif")) {
            return true;
        } else if (mode.contains("off") || mode.contains("false") || mode.contains("kapat") || mode.contains("de-aktif") || mode.contains("deaktif")) {
            return false;
        }
        return false;
    }

}
