package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.commands.*;
import io.github.bilektugrul.simpleservertools.commands.gamemode.GamemodeCommand;
import io.github.bilektugrul.simpleservertools.commands.msg.MessageCommand;
import io.github.bilektugrul.simpleservertools.commands.msg.MessageToggleCommand;
import io.github.bilektugrul.simpleservertools.commands.spawn.SetSpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.spawn.SpawnCommand;
import io.github.bilektugrul.simpleservertools.commands.speed.SpeedCommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPAAcceptCommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPACommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPADenyCommand;
import io.github.bilektugrul.simpleservertools.commands.tpa.TPAToggleCommand;
import io.github.bilektugrul.simpleservertools.features.announcements.AnnouncementManager;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.listeners.PlayerListener;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.PLibManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SST extends JavaPlugin {

    private CustomPlaceholderManager placeholderManager;
    private WarpManager warpManager;
    private UserManager userManager;
    private SpawnManager spawnManager;
    private JoinMessageManager joinMessageManager;
    private VaultManager vaultManager;
    private VanishManager vanishManager;
    private TPAManager tpaManager;
    private TeleportManager teleportManager;
    private AnnouncementManager announcementManager;
    private MaintenanceManager maintenanceManager;

    private boolean forceDisabled = false;

    private AsyncUserSaveThread asyncUserSaveThread;

    private PluginManager pluginManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        ConsoleCommandSender console = getServer().getConsoleSender();
        pluginManager = getServer().getPluginManager();
        if (checkLicence()) {
            console.sendMessage("Lisans geçerli. Satın alımınız için teşekkürler");
            placeholderManager = new CustomPlaceholderManager(this);
            userManager = new UserManager(this);
            teleportManager = new TeleportManager(this);
            warpManager = new WarpManager(this);
            spawnManager = new SpawnManager(this);
            joinMessageManager = new JoinMessageManager(this);
            tpaManager = new TPAManager(this);
            maintenanceManager = new MaintenanceManager(this);
            vanishManager = new VanishManager();
            if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
                new PAPIPlaceholders(this).register();
            } else {
                getLogger().warning("PlaceholderAPI bulunamadı. Binlerce placeholderı rahatça kullanabilmek için indirmenizi öneririz.");
            }
            if (pluginManager.isPluginEnabled("Vault")) {
                vaultManager = new VaultManager(this);
            } else {
                getLogger().warning("Vault bulunamadı. Gruplara ve permissionlara özel bazı özellikler çalışmayabilir.");
            }
            announcementManager = new AnnouncementManager(this);
            for (Player looped : Bukkit.getOnlinePlayers()) {
                userManager.loadUser(looped);
            }
            pluginManager.registerEvents(new PlayerListener(this), this);
            getCommand("simpleservertools").setExecutor(new SSTCommand());
            getCommand("gamemode").setExecutor(new GamemodeCommand());
            getCommand("fly").setExecutor(new FlyCommand());
            getCommand("broadcast").setExecutor(new BroadcastCommand());
            getCommand("disposal").setExecutor(new DisposalCommand());
            getCommand("ping").setExecutor(new PingCommand());
            getCommand("feed").setExecutor(new FeedCommand());
            getCommand("heal").setExecutor(new HealCommand());
            getCommand("invsee").setExecutor(new InvSeeCommand());
            getCommand("kick").setExecutor(new KickCommand());
            getCommand("tpall").setExecutor(new TPAllCommand());
            getCommand("speed").setExecutor(new SpeedCommand());
            getCommand("enderchest").setExecutor(new EnderChestCommand());
            getCommand("clearchat").setExecutor(new ClearChatCommand());
            getCommand("craft").setExecutor(new CraftCommand());
            getCommand("msg").setExecutor(new MessageCommand(this));
            getCommand("msgtoggle").setExecutor(new MessageToggleCommand(this));
            getCommand("vanish").setExecutor(new VanishCommand(this));
            getCommand("warp").setExecutor(new WarpCommand(this));
            getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
            getCommand("spawn").setExecutor(new SpawnCommand(this));
            getCommand("god").setExecutor(new GodCommand(this));
            getCommand("tpa").setExecutor(new TPACommand(this));
            getCommand("tpaaccept").setExecutor(new TPAAcceptCommand(this));
            getCommand("tpadeny").setExecutor(new TPADenyCommand(this));
            getCommand("tpatoggle").setExecutor(new TPAToggleCommand(this));
            getCommand("skull").setExecutor(new SkullCommand(this));
            getCommand("maintenance").setExecutor(new MaintenanceCommand(this));
            reload(true);
            if (Utils.getBoolean("auto-save-users")) {
                asyncUserSaveThread = new AsyncUserSaveThread(this);
            }
        } else {
            console.sendMessage("Lisans geçersiz.");
            forceDisabled = true;
            getServer().shutdown();
        }
    }

    private boolean checkLicence() {
        License license = new License(getConfig().getString("lisans"), "https://vantoxicdesign.com/", this);
        license.request();
        getServer().getConsoleSender().sendMessage("Lisans kontrol ediliyor: " + license.getLicense());
        return license.isValid();
    }

    @Override
    public void onDisable() {
        if (!forceDisabled) {
            getServer().getScheduler().cancelTasks(this);
            try {
                userManager.saveUsers();
            } catch (IOException e) {
                e.printStackTrace();
            }
            warpManager.saveWarps();
            spawnManager.saveSpawn();
            maintenanceManager.save();
            ConfigUtils.saveConfig(this, getConfig(), "config"); // Sadly, all of the comments will be gone after this. Maybe using custom YAML library would fix this.
            getLogger().info("Tüm veriler kaydedildi.");
        }
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

    public AnnouncementManager getAnnouncementManager() {
        return announcementManager;
    }

    public MaintenanceManager getMaintenanceManager() {
        return maintenanceManager;
    }

    public void checkAndLoadPacketListener() {
        if (pluginManager.isPluginEnabled("ProtocolLib")) {
            PLibManager.loadPacketListener(this);
        }
    }

    public void reload(boolean first) {
        reloadConfig();
        checkAndLoadPacketListener();
        placeholderManager.load();
        if (!first) {
            announcementManager.reload();
            warpManager.reloadWarps();
            warpManager.loadSettings();
            spawnManager.reloadSpawn();
            joinMessageManager.reload();
            tpaManager.loadSettings();
            maintenanceManager.reload();
            if (Utils.getBoolean("auto-save-users")) {
                asyncUserSaveThread = new AsyncUserSaveThread(this);
            } else if (asyncUserSaveThread != null) {
                asyncUserSaveThread.cancel();
            }
        }
    }

}
