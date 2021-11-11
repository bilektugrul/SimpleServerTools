package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.HomeManager;
import io.github.bilektugrul.simpleservertools.features.language.LanguageManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.stuff.MessageType;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.MessageMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMessage;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserState;
import me.clip.placeholderapi.PlaceholderAPI;
import me.despical.commons.compat.Titles;
import me.despical.commons.compat.VersionResolver;
import me.despical.commons.util.Strings;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

public class Utils {

    private Utils() {}

    private static final SST plugin = JavaPlugin.getPlugin(SST.class);
    private static HomeManager homeManager;
    private static final CustomPlaceholderManager placeholderManager = plugin.getPlaceholderManager();
    private static final LanguageManager languageManager = plugin.getLanguageManager();

    private static final boolean isPAPIEnabled = Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");

    public static void noPermission(CommandSender sender) {
        sender.sendMessage(getMessage("no-permission", sender));
    }

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
        msg = placeholderManager.replacePlaceholders(Strings.format(msg))
                .replace("%nl%", "\n");
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

    public static String getString(String string) {
        return plugin.getConfig().getString(string);
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

    public static float getFloat(YamlConfiguration yaml, String path) {
        return Float.parseFloat(yaml.getString(path));
    }

    public static int getLanguageInt(String path) {
        return languageManager.getLanguage().getInt(path);
    }

    // made by hakan-krgn
    public static int getMaximum(Player player, String perm, int def) {
        TreeSet<Integer> permMax = new TreeSet<>();
        for (PermissionAttachmentInfo permissionAttachmentInfo : player.getEffectivePermissions()) {
            String permission = permissionAttachmentInfo.getPermission();
            if (permission.contains(perm)) {
                permMax.add(Integer.parseInt(permission.replace(perm, "")));
            }
        }
        return permMax.size() != 0 ? permMax.last() : def;
    }

    public static Location getLocation(YamlConfiguration yaml, String key) {
        return (Location) yaml.get(key);
    }

    public static boolean isSameLoc(Location loc1, Location loc2) {
        return (loc1.getBlockX() == loc2.getBlockX()) && (loc1.getBlockY() == loc2.getBlockY()) && (loc1.getBlockZ() == loc2.getBlockZ());
    }

    public static void sendMessage(Player p, User user, MessageType messageType, TeleportMessage message, TeleportMode teleportMode, String time) {
        MessageMode messageMode = message.getMessageMode();
        boolean isTeleporting = messageMode == MessageMode.TELEPORTING;
        String chatMessage = isTeleporting
                ? replaceEverything(user, message.getTeleportingChat(), teleportMode, time)
                : replaceEverything(user, message.getTeleportedChat(), teleportMode, time);
        String title = isTeleporting
                ? replaceEverything(user, message.getTeleportingTitle(), teleportMode, time)
                : replaceEverything(user, message.getTeleportedTitle(), teleportMode, time);
        String subtitle = isTeleporting
                ? replaceEverything(user, message.getTeleportingSub(), teleportMode, time)
                : replaceEverything(user, message.getTeleportedSub(), teleportMode, time);
        String actionBar = isTeleporting
                ? replaceEverything(user, message.getTeleportingActionBar(), teleportMode, time)
                : replaceEverything(user, message.getTeleportedActionBar(), teleportMode, time);
        switch (messageType) {
            case CHAT -> p.sendMessage(chatMessage);
            case TITLE -> Titles.sendTitle(p, title, subtitle, getInt("titles.fade-in"), getInt("titles.stay"), getInt("titles-fade-out"));
            case ACTIONBAR -> sendActionBar(p, actionBar);
            case CHAT_AND_TITLE -> {
                p.sendMessage(chatMessage);
                Titles.sendTitle(p, title, subtitle, getInt("titles.fade-in"), getInt("titles.stay"), getInt("titles-fade-out"));
            }
            case BAR_AND_TITLE -> {
                sendActionBar(p, actionBar);
                Titles.sendTitle(p, title, subtitle, getInt("titles.fade-in"), getInt("titles.stay"), getInt("titles-fade-out"));
            }
            case CHAT_AND_BAR -> {
                p.sendMessage(chatMessage);
                sendActionBar(p, actionBar);
            }
            case ALL -> {
                p.sendMessage(chatMessage);
                Titles.sendTitle(p, title, subtitle, getInt("titles.fade-in"), getInt("titles.stay"), getInt("titles-fade-out"));
                sendActionBar(p, actionBar);
            }
        }
    }

    public static String replaceEverything(User user, String string, TeleportMode teleportMode, String time) {
        switch (teleportMode.mode()) {
            case WARPS -> {
                String name = teleportMode.warp().name();
                string = string.replace("%warp%", name);
            }
            case SPAWN -> user.setState(UserState.TELEPORTING_SPAWN);
            case HOMES -> {
                String name = teleportMode.home().name();
                string = string.replace("%home%", name);
            }
            default -> {
                String teleportingTo = teleportMode.tpaInfo().toTeleport().getName();
                string = string.replace("%teleporting%", teleportingTo);
            }
        }
        return string.replace("%time%", time);
    }

    public static void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(getMessage( msg + ".beginning", sender));
        if (sender.hasPermission("sst.staff")) {
            sender.sendMessage(getMessage(msg + ".only-staff", sender));
        }
        sender.sendMessage(getMessage(msg + ".everyone", sender));
        sender.sendMessage(getMessage(msg + ".ending", sender));
    }

    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        String nmsVersion = Bukkit.getServer().getClass().getPackage().getName();
        nmsVersion = nmsVersion.substring(nmsVersion.lastIndexOf(".") + 1);

        //1.10 and up
        if (!nmsVersion.startsWith("v1_9_R") && !nmsVersion.startsWith("v1_8_R")) {
            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            return;
        }

        //1.8.x and 1.9.x
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nmsVersion + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);

            Class<?> ppoc = Class.forName("net.minecraft.server." + nmsVersion + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nmsVersion + ".Packet");
            Object packetPlayOutChat;
            Class<?> chat = Class.forName("net.minecraft.server." + nmsVersion + (nmsVersion.equalsIgnoreCase("v1_8_R1") ? ".ChatSerializer" : ".ChatComponentText"));
            Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nmsVersion + ".IChatBaseComponent");

            Method method = null;
            if (nmsVersion.equalsIgnoreCase("v1_8_R1")) method = chat.getDeclaredMethod("a", String.class);

            Object object = nmsVersion.equalsIgnoreCase("v1_8_R1") ? chatBaseComponent.cast(method.invoke(chat, "{'text': '" + message + "'}")) : chat.getConstructor(new Class[]{String.class}).newInstance(message);
            packetPlayOutChat = ppoc.getConstructor(new Class[]{chatBaseComponent, Byte.TYPE}).newInstance(object, (byte) 2);

            Method handle = craftPlayerClass.getDeclaredMethod("getHandle");
            Object iCraftPlayer = handle.invoke(craftPlayer);
            Field playerConnectionField = iCraftPlayer.getClass().getDeclaredField("playerConnection");
            Object playerConnection = playerConnectionField.get(iCraftPlayer);
            Method sendPacket = playerConnection.getClass().getDeclaredMethod("sendPacket", packet);
            sendPacket.invoke(playerConnection, packetPlayOutChat);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public static String listToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append("\n");
        }
        return builder.toString();
    }

    public static int getMaxHomeAmount(Player userPlayer) {
        if (homeManager == null) homeManager = plugin.getHomeManager();
        return getMaximum(userPlayer, "sst.homes.", homeManager.getDefaultMaxHomeAmount());
    }

    //Author: Alpho320
    public static String took(long from) {
        return tookThisLong(from) + "ms";
    }

    //Author: Alpho320
    public static long tookThisLong(long from) {
        return System.currentTimeMillis() - from;
    }

}