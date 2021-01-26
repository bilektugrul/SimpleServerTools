package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.commands.*;
import io.github.bilektugrul.simpleservertools.listeners.*;
import io.github.bilektugrul.simpleservertools.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.warps.WarpManager;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SimpleServerTools extends JavaPlugin {

    private final List<UUID> vanishedPlayers = new ArrayList<>();
    private final List<UUID> onlineVanishedPlayers = new ArrayList<>();

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<UUID> getOnlineVanishedPlayers() {
        return onlineVanishedPlayers;
    }

    private WarpManager warpManager;

    public static String prefix = "";
    public static String warpPrefix = "";
    public static String adminPerm = "";
    public static String vanishPerm = "";
    public static String vanishOthersPerm = "";
    public static String listPerm = "";
    public static String staffPerm = "";


    @Override
    public void onEnable() {
        saveDefaultConfig();
        warpManager = new WarpManager(this);
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getCommand("simpleservertools").setExecutor(new SSTCommand());
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("vanish").setExecutor(new VanishCommand());
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIPlaceholders(this).register();
        }
        reload();
    }

    @Override
    public void onDisable() {
        warpManager.saveWarps();
        getLogger().info("Warps saved.");
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public void checkAndLoadPacketListener() {
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            PLibManager.loadPacketListener(this);
        }
    }

    public void reload() {
        checkAndLoadPacketListener();
        reloadConfig();
        prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefixes.global"));
        warpPrefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefixes.warps"));
        adminPerm = Utils.getString("permissions.admin-permission");
        vanishPerm = Utils.getString("permissions.vanish.permission");
        vanishOthersPerm = Utils.getString("permissions.vanish.permission-others");
        listPerm = Utils.getString("permissions.list-command-permission");
        staffPerm = Utils.getString("permissions.staff-permission");
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Utils.getString("other-messages." + msg + ".beginning"));
        if (sender.hasPermission(staffPerm)) {
            sender.sendMessage(Utils.getString("other-messages." + msg + ".only-staff"));
        }
        sender.sendMessage(Utils.getString("other-messages." + msg + ".everyone"));
        sender.sendMessage(Utils.getString("other-messages." + msg + ".ending"));
    }

}
