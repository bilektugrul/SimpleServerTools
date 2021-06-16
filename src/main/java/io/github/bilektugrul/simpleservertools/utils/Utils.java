package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.language.LanguageManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.stuff.MessageType;
import me.clip.placeholderapi.PlaceholderAPI;
import me.despical.commons.compat.Titles;
import me.despical.commons.compat.VersionResolver;
import me.despical.commons.util.Strings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class Utils {

    private Utils() {}

    private static final SST plugin = JavaPlugin.getPlugin(SST.class);
    private static final CustomPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
    private static final LanguageManager languageManager = plugin.getLanguageManager();

    private static final boolean isPAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    public static String getMessage(String string) {
        return getString(languageManager.getLanguage(), "messages." + string, null, false, false);
    }

    public static String getMessage(String string, CommandSender from) {
        return getString(languageManager.getLanguage(), "messages." + string, from);
    }

    public static String getMessage(String string, CommandSender from, boolean replacePersonalPlaceholders) {
        return getString(languageManager.getLanguage(), "messages." + string, from, replacePersonalPlaceholders);
    }

    public static String getString(String string, CommandSender from) {
        return replacePlaceholders(plugin.getConfig().getString(string), from, true);
    }

    public static String getString(String string, CommandSender from, boolean replacePersonalPlaceholders) {
        return replacePlaceholders(plugin.getConfig().getString(string), from, replacePersonalPlaceholders);
    }

    public static String getString(FileConfiguration file, String string, CommandSender from) {
        return replacePlaceholders(file.getString(string), from, true);
    }

    public static String getString(FileConfiguration file, String string, CommandSender from, boolean replacePersonalPlaceholders) {
        return replacePlaceholders(file.getString(string), from, replacePersonalPlaceholders);
    }

    public static String getString(FileConfiguration file, String string, CommandSender from, boolean replacePersonalPlaceholders, boolean replacePAPI) {
        return replacePlaceholders(file.getString(string), from, replacePersonalPlaceholders, replacePAPI);
    }

    public static String replacePlaceholders(String msg, CommandSender from, boolean replacePersonalPlaceholders, boolean replacePAPI) {
        boolean isPlayer = from instanceof Player;
        if (msg == null) {
            plugin.getLogger().warning(org.bukkit.ChatColor.RED + "Your language file[s] is/are corrupted or old. Please reset or update them.");
            return "";
        }
        msg = placeholderManager.replacePlaceholders(Strings.format(msg));
        if (replacePersonalPlaceholders) {
            msg = msg.replace("%player%", matchName(from));
        }
        if (isPAPIEnabled && replacePAPI) {
            return PlaceholderAPI.setPlaceholders(isPlayer ? (Player) from : null, msg);
        }
        return msg;
    }

    public static String replacePlaceholders(String msg, CommandSender from, boolean replacePersonalPlaceholders) {
        return replacePlaceholders(msg, from, replacePersonalPlaceholders, true);
    }

    public static String matchName(CommandSender entity) {
        return entity instanceof Player ? entity.getName() : "CONSOLE";
    }

    public static boolean getBoolean(String string, boolean def) {
        return plugin.getConfig().getBoolean(string, def);
    }

    public static boolean getBoolean(String string) {
        return plugin.getConfig().getBoolean(string);
    }

    public static boolean getLanguageBoolean(String string) {
        return languageManager.getBoolean(string);
    }

    public static int getInt(String path) {
        return plugin.getConfig().getInt(path);
    }

    public static boolean getBoolean(FileConfiguration file, String string, boolean def) {
        return file.getBoolean(string, def);
    }

    public static boolean getBoolean(FileConfiguration file, String string) {
        return file.getBoolean(string);
    }

    public static int getInt(FileConfiguration file, String path) {
        return file.getInt(path);
    }

    public static int getLanguageInt(String path) {
        return languageManager.getLanguage().getInt(path);
    }

    public static boolean isSameLoc(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX()) && (loc1.getBlockY() == loc2.getBlockY()) && (loc1.getBlockZ() == loc2.getBlockZ());
    }

    public static void sendMessage(Player p, MessageType mode, String msg, String subtitle, String time) {
        msg = msg.replace("%time%", time);
        subtitle = subtitle.replace("%time%", time) ;
        switch (mode) {
            case CHAT:
                p.sendMessage(msg);
                break;
            case TITLE:
                Titles.sendTitle(p, msg, subtitle, getInt("titles.fade-in"), getInt("titles.stay"), getInt("titles-fade-out"));
                break;
            case ACTIONBAR:
                ActionBar.sendActionBar(p, msg.replace('\n', ' '));
                break;
        }
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(getMessage( msg + ".beginning", sender));
        if (sender.hasPermission("sst.staff")) {
            sender.sendMessage(getMessage(msg + ".only-staff", sender));
        }
        sender.sendMessage(getMessage(msg + ".everyone", sender));
        sender.sendMessage(getMessage(msg + ".ending", sender));
    }

    public static int getPing(Player player) {
        if (VersionResolver.isCurrentEqualOrHigher(VersionResolver.ServerVersion.v1_16_R1)) {
            return player.getPing();
        } else {
            return player.spigot().getPing();
        }
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

    public static String arrayToString(String[] array, CommandSender sender, boolean replacePersonalPlaceholders, boolean replacePAPI) {
        String str = String.join(" ", array);
        return replacePlaceholders(str, sender, replacePersonalPlaceholders, replacePAPI);
    }

}
