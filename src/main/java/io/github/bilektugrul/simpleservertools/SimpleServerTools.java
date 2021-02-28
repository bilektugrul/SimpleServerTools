package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.commands.*;
import io.github.bilektugrul.simpleservertools.commands.spawn.SetSpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.spawn.SpawnCommand;
import io.github.bilektugrul.simpleservertools.features.joinmessage.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.listeners.PlayerListener;
import io.github.bilektugrul.simpleservertools.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
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
    private UserManager userManager;
    private SpawnManager spawnManager;
    private JoinMessageManager joinMessageManager;

    public static String prefix = "";
    public static String warpPrefix = "";
    public static String spawnPrefix = "";

    public static String adminPerm = "";
    public static String vanishPerm = "";
    public static String vanishOthersPerm = "";
    public static String listPerm = "";
    public static String staffPerm = "";

    private VaultManager vaultManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PaperLib.suggestPaper(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        userManager = new UserManager();
        joinMessageManager = new JoinMessageManager(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIPlaceholders(this).register();
        } else {
            getLogger().warning("PlaceholderAPI couldn't found. You should check it out.");
        }
        if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
            vaultManager = new VaultManager(this);
        } else {
            getLogger().warning("Vault couldn't found. Permission based features probably will not work.");
        }
        getServer().getPluginManager().registerEvents(new PlayerListener(this, vaultManager), this);
        getCommand("simpleservertools").setExecutor(new SSTCommand());
        getCommand("vanish").setExecutor(new VanishCommand());
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("disposal").setExecutor(new DisposalCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        reload(true);
    }

    @Override
    public void onDisable() {
        warpManager.saveWarps();
        spawnManager.saveSpawn();
        getLogger().info("Warps and spawn saved.");
    }

    public WarpManager getWarpManager() {
        return warpManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public Permission getPermissionManager() {
        return vaultManager.getPermissionProvider();
    }

    public boolean isPermManagerReady() {
        return vaultManager != null;
    }

    public JoinMessageManager getJoinMessageManager() {
        return joinMessageManager;
    }

    public void checkAndLoadPacketListener() {
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            PLibManager.loadPacketListener(this);
        }
    }

    public void reload(boolean first) {
        reloadConfig();
        checkAndLoadPacketListener();
        FileConfiguration config = getConfig();
        //TODO: PREFIX SISTEMI DEGISECEK
        prefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefixes.global"));
        warpPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefixes.warps"));
        spawnPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("prefixes.spawn"));
        //TODO: PREFIX SISTEMI DEGISECEK
        adminPerm = config.getString("permissions.admin-permission");
        vanishPerm = config.getString("permissions.vanish.permission");
        vanishOthersPerm = config.getString("permissions.vanish.permission-others");
        listPerm = config.getString("permissions.list-command-permission");
        staffPerm = config.getString("permissions.staff-permission");
        if (!first) {
            warpManager.reloadWarps();
            spawnManager.reloadSpawn();
            joinMessageManager.reload();
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Utils.getString("other-messages." + msg + ".beginning", sender));
        if (sender.hasPermission(staffPerm)) {
            sender.sendMessage(Utils.getString("other-messages." + msg + ".only-staff", sender));
        }
        sender.sendMessage(Utils.getString("other-messages." + msg + ".everyone", sender));
        sender.sendMessage(Utils.getString("other-messages." + msg + ".ending", sender));
    }

}
