package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.commands.*;
import io.github.bilektugrul.simpleservertools.commands.spawn.SetSpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.spawn.SpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPAAcceptCommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPACommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPADenyCommand;
import io.github.bilektugrul.simpleservertools.features.custom.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.features.joinmessage.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.listeners.PlayerListener;
import io.github.bilektugrul.simpleservertools.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.PLibManager;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: Add /tpaccept and /tpadeny
public class SimpleServerTools extends JavaPlugin {

    private CustomPlaceholderManager placeholderManager;
    private WarpManager warpManager;
    private UserManager userManager;
    private SpawnManager spawnManager;
    private JoinMessageManager joinMessageManager;
    private VaultManager vaultManager;
    private VanishManager vanishManager;
    private TPAManager tpaManager;
    private TeleportManager teleportManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        PaperLib.suggestPaper(this);
        placeholderManager = new CustomPlaceholderManager(this);
        teleportManager = new TeleportManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        userManager = new UserManager();
        joinMessageManager = new JoinMessageManager(this);
        vanishManager = new VanishManager();
        tpaManager = new TPAManager(this);
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
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getCommand("simpleservertools").setExecutor(new SSTCommand());
        getCommand("vanish").setExecutor(new VanishCommand(vanishManager));
        getCommand("gamemode").setExecutor(new GamemodeCommand());
        getCommand("fly").setExecutor(new FlyCommand());
        getCommand("broadcast").setExecutor(new BroadcastCommand());
        getCommand("disposal").setExecutor(new DisposalCommand());
        getCommand("ping").setExecutor(new PingCommand());
        getCommand("feed").setExecutor(new FeedCommand());
        getCommand("heal").setExecutor(new HealCommand());
        getCommand("invsee").setExecutor(new InvSeeCommand());
        getCommand("kick").setExecutor(new KickCommand());
        getCommand("warp").setExecutor(new WarpCommand(this));
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("god").setExecutor(new GodCommand(this));
        getCommand("tpa").setExecutor(new TPACommand(this));
        getCommand("tpaaccept").setExecutor(new TPAAcceptCommand(this));
        getCommand("tpadeny").setExecutor(new TPADenyCommand(this));
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

    public VaultManager getVaultManager() {
        return vaultManager;
    }

    public boolean isPermManagerReady() {
        return vaultManager != null;
    }

    public JoinMessageManager getJoinMessageManager() {
        return joinMessageManager;
    }

    public VanishManager getVanishManager() {
        return vanishManager;
    }

    public TPAManager getTPAManager() {
        return tpaManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public void checkAndLoadPacketListener() {
        if (getServer().getPluginManager().isPluginEnabled("ProtocolLib")) {
            PLibManager.loadPacketListener(this);
        }
    }

    public void reload(boolean first) {
        reloadConfig();
        checkAndLoadPacketListener();
        placeholderManager.load();
        if (!first) {
            warpManager.reloadWarps();
            spawnManager.reloadSpawn();
            joinMessageManager.reload();
        }
    }

}
