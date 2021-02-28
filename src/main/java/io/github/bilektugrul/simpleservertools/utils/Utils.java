package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.stuff.ActionBar;
import io.github.bilektugrul.simpleservertools.stuff.CancelModes;
import io.github.bilektugrul.simpleservertools.stuff.TeleportMode;
import io.github.bilektugrul.simpleservertools.stuff.TeleportSettings;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.papermc.lib.PaperLib;
import me.clip.placeholderapi.PlaceholderAPI;
import me.despical.commonsbox.compat.Titles;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Utils {

    private static final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);
    private static final WarpManager warpManager = plugin.getWarpManager();
    private static final UserManager userManager = plugin.getUserManager();
    private static final SpawnManager spawnManager = plugin.getSpawnManager();

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

    public static String getString(String string, Object from) {
        String msg = colorMessage(plugin.getConfig().getString(string));
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            if (from instanceof Player) msg = replacePlaceholders(msg, (Player) from, true);
            else msg = PlaceholderAPI.setPlaceholders(null, msg);
        }
        return msg;
    }

    public static String replacePlaceholders(String msg, Player player, boolean direct) {
        msg = colorMessage(msg).replace("%player%", player.getName());
        if (direct || Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            return PlaceholderAPI.setPlaceholders(player, msg);
        }
        return msg;
    }

    public static String colorMessage(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg)
                .replace("%prefix%", SimpleServerTools.prefix)
                .replace("%prefix:warp%", SimpleServerTools.warpPrefix)
                .replace("%prefix:spawn%", SimpleServerTools.spawnPrefix);
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
        subtitle = subtitle.replace("%time%", time);
        switch (mode) {
            case "CHAT":
                p.sendMessage(msg);
                break;
            case "TITLE":
                Titles.sendTitle(p, msg, subtitle);
                break;
            case "ACTIONBAR":
                ActionBar.sendActionBar(p, msg);
                break;
        }
    }

    public static void teleport(Player p, Location loc, final TeleportMode teleportMode) {
        new BukkitRunnable() {
            final TeleportSettings settings;
            final String mode = teleportMode.getModeString();
            {
                if (teleportMode.getMode() == TeleportMode.Mode.WARPS)
                    settings = warpManager.getSettings();
                else
                    settings = spawnManager.getSettings();
            }
            int time = p.hasPermission(SimpleServerTools.staffPerm) && Utils.getBoolean(mode + ".staff-bypass-time")
                    ? 0
                    : settings.getTime();

            String teleportingMsg = Utils.getString("other-messages." + mode + ".teleporting.message", p);
            final String teleportingMode = Utils.getString("other-messages." + mode + ".teleporting.mode", p);
            String teleportingSub = "";

            String teleportedMsg = Utils.getString("other-messages." + mode + ".teleported.message", p);
            final String teleportedMode = Utils.getString("other-messages." + mode +".teleported.mode", p);
            String teleportedSub = "";

            final Location firstLoc = p.getLocation();
            final double firstHealth = p.getHealth();

            final UUID uuid = p.getUniqueId();

            final CancelModes cancelMoveMode = settings.getCancelMoveMode();
            final CancelModes cancelDamageMode = settings.getCancelDamageMode();
            final User user = userManager.getUser(uuid);

            {
                if (teleportMode.getMode() == TeleportMode.Mode.WARPS) {
                    teleportedMsg = teleportedMsg.replace("%warp%", teleportMode.getWarp().getName());
                }
                user.setState(User.State.TELEPORTING_SPAWN);
                if ((teleportingMode.equalsIgnoreCase("TITLE") && teleportingMsg.contains("\n"))) {
                    int index = teleportingMsg.indexOf("\n");
                    teleportingSub = teleportingMsg.split("\n")[1];
                    teleportingMsg = teleportingMsg.substring(0, index);
                }
                if (teleportedMode.equalsIgnoreCase("TITLE") && teleportedMsg.contains("\n")) {
                    int index = teleportedMsg.indexOf("\n");
                    teleportedSub  = teleportedMsg.split("\n")[1];
                    teleportedMsg = teleportedMsg.substring(0, index);
                }
            }

            @Override
            public void run() {

                if (!Utils.isSameLoc(firstLoc, p.getLocation())) {
                    boolean cancel = cancelMoveMode == CancelModes.EVERYONE
                            || (cancelMoveMode == CancelModes.STAFF && p.hasPermission(SimpleServerTools.staffPerm));
                    if (cancel) {
                        if (settings.getCancelTeleportOnMove()) {
                            cancelTeleport(user, this, p);
                            return;
                        }
                        if (settings.getBlockMove()) p.teleport(firstLoc);
                    }
                }

                if (p.getHealth() != firstHealth) {
                    boolean cancel = cancelDamageMode == CancelModes.EVERYONE
                            || (cancelDamageMode == CancelModes.STAFF && p.hasPermission(SimpleServerTools.staffPerm));
                    if (cancel) {
                        if (settings.getBlockDamage()) {
                            p.setHealth(firstHealth);
                            return;
                        }
                        if (settings.getCancelTeleportOnDamage()) Utils.cancelTeleport(user, this, p);
                    }
                }

                if (time == 0) {
                    user.setState(User.State.PLAYING);
                    PaperLib.teleportAsync(p, loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                    Utils.sendMessage(p, teleportedMode, teleportedMsg, teleportedSub, String.valueOf(time));
                    cancel();
                    return;
                }

                Utils.sendMessage(p, teleportingMode, teleportingMsg, teleportingSub, String.valueOf(time));
                time--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    public static void cancelTeleport(User user, BukkitRunnable runnable, Player p) {
        user.setState(User.State.PLAYING);
        runnable.cancel();
        p.sendMessage(Utils.getString("other-messages.warps.teleport-cancelled", p));
    }

}
