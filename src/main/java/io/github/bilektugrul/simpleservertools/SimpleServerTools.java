package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.commands.*;
import io.github.bilektugrul.simpleservertools.commands.spawn.SetSpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.spawn.SpawnCommand;
import io.github.bilektugrul.simpleservertools.features.custom.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.features.joinmessage.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.listeners.PlayerListener;
import io.github.bilektugrul.simpleservertools.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.PLibManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import io.papermc.lib.PaperLib;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//TODO: VanishManager
public class SimpleServerTools extends JavaPlugin {

    private final List<UUID> vanishedPlayers = new ArrayList<>();
    private final List<UUID> onlineVanishedPlayers = new ArrayList<>();

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }

    public List<UUID> getOnlineVanishedPlayers() {
        return onlineVanishedPlayers;
    }

    private CustomPlaceholderManager placeholderManager;
    private WarpManager warpManager;
    private UserManager userManager;
    private SpawnManager spawnManager;
    private JoinMessageManager joinMessageManager;
    private VaultManager vaultManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PaperLib.suggestPaper(this);
        placeholderManager = new CustomPlaceholderManager(this);
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
        getCommand("invsee").setExecutor(new InvSeeCommand());
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

    public CustomPlaceholderManager getPlaceholderManager() {
        return placeholderManager;
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
        placeholderManager.load();
        if (!first) {
            warpManager.reloadWarps();
            spawnManager.reloadSpawn();
            joinMessageManager.reload();
        }
    }

    public void sendMessage(CommandSender sender, String msg) {
        sender.sendMessage(Utils.getString("other-messages." + msg + ".beginning", sender));
        if (sender.hasPermission("sst.staff")) {
            sender.sendMessage(Utils.getString("other-messages." + msg + ".only-staff", sender));
        }
        sender.sendMessage(Utils.getString("other-messages." + msg + ".everyone", sender));
        sender.sendMessage(Utils.getString("other-messages." + msg + ".ending", sender));
    }

}
