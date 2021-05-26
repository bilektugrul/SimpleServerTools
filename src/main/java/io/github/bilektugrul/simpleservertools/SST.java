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
import io.github.bilektugrul.simpleservertools.commands.warp.DelWarpCommand;
import io.github.bilektugrul.simpleservertools.commands.warp.SetWarpCommand;
import io.github.bilektugrul.simpleservertools.commands.warp.WarpCommand;
import io.github.bilektugrul.simpleservertools.converting.ConverterManager;
import io.github.bilektugrul.simpleservertools.features.announcements.AnnouncementManager;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.language.LanguageManager;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.CustomPlaceholderManager;
import io.github.bilektugrul.simpleservertools.features.placeholders.PAPIPlaceholders;
import io.github.bilektugrul.simpleservertools.features.rules.RulesManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.spy.SpyManager;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.listeners.PlayerListener;
import io.github.bilektugrul.simpleservertools.metrics.Metrics;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.PLibManager;
import io.github.bilektugrul.simpleservertools.utils.UpdateChecker;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SST extends JavaPlugin {

    private CustomPlaceholderManager placeholderManager;
    private LanguageManager languageManager;
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
    private SpyManager spyManager;
    private ConverterManager converterManager;
    private RulesManager rulesManager;

    private AsyncUserSaveThread asyncUserSaveThread;

    private PluginManager pluginManager;

    private Logger logger;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();
        saveDefaultConfig();
        logger = getLogger();
        logger.info(ChatColor.GREEN + "SimpleServerTools v" + getDescription().getVersion() + " is being enabled. Thanks for using SST!");

        registerManagers();

        for (Player looped : Bukkit.getOnlinePlayers()) {
            userManager.loadUser(looped);
        }

        pluginManager.registerEvents(new PlayerListener(this), this);

        registerCommands();

        reload(true);
        if (Utils.getBoolean("auto-save-users")) {
            asyncUserSaveThread = new AsyncUserSaveThread(this);
        }
        if (Utils.getBoolean("metrics-enabled")) {
            logger.info(ChatColor.GREEN + "Enabling metrics...");
            new Metrics(this, 11344);
        }

        checkUpdate();

        logger.log(Level.INFO, ChatColor.GREEN + "Enabling process is done, enjoy! " + ChatColor.AQUA + "{0} ms", System.currentTimeMillis() - start);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);
        try {
            userManager.saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
        warpManager.saveWarps();
        spawnManager.saveSpawn();
        maintenanceManager.save();
        logger.info(ChatColor.GREEN + "Everything has been saved.");
    }

    private void registerManagers() {
        pluginManager = getServer().getPluginManager();
        placeholderManager = new CustomPlaceholderManager(this);
        languageManager = new LanguageManager(this);
        userManager = new UserManager(this);
        teleportManager = new TeleportManager(this);
        warpManager = new WarpManager(this);
        spawnManager = new SpawnManager(this);
        joinMessageManager = new JoinMessageManager(this);
        tpaManager = new TPAManager(this);
        maintenanceManager = new MaintenanceManager(this);
        rulesManager = new RulesManager(this);

        vanishManager = new VanishManager();
        spyManager = new SpyManager();

        if (pluginManager.isPluginEnabled("PlaceholderAPI")) {
            new PAPIPlaceholders(this).register();
        } else {
            logger.warning(ChatColor.RED + "PlaceholderAPI is not installed. You should check it out.");
        }
        if (pluginManager.isPluginEnabled("Vault")) {
            vaultManager = new VaultManager(this);
        } else {
            logger.warning(ChatColor.RED + "Vault is not installed. Some features may not work.");
        }

        announcementManager = new AnnouncementManager(this);
    }

    private void registerCommands() {
        getCommand("simpleservertools").setExecutor(new SSTCommand()); // Main command can not be disabled.

        registerCommand(new GamemodeCommand(), "gamemode");
        registerCommand(new FlyCommand(), "fly");
        registerCommand(new BroadcastCommand(), "broadcast");
        registerCommand(new DisposalCommand(), "disposal");
        registerCommand(new PingCommand(), "ping");
        registerCommand(new FeedCommand(), "feed");
        registerCommand(new HealCommand(), "heal");
        registerCommand(new InvSeeCommand(), "invsee");
        registerCommand(new KickCommand(), "kick");
        registerCommand(new TPAllCommand(), "tpall");
        registerCommand(new SpeedCommand(), "speed");
        registerCommand(new EnderChestCommand(), "enderchest");
        registerCommand(new ClearChatCommand(), "clearchat");
        registerCommand(new CraftCommand(), "craft");
        registerCommand(new TopCommand(), "top");
        registerCommand(new TPHereCommand(), "tphere");

        registerCommand(new MessageCommand(this), "msg");
        registerCommand(new MessageToggleCommand(this), "msgtoggle");
        registerCommand(new VanishCommand(this), "vanish");
        registerCommand(new WarpCommand(this), "warp");
        registerCommand(new SetWarpCommand(this), "setwarp");
        registerCommand(new DelWarpCommand(this), "delwarp");
        registerCommand(new SetSpawnCommand(this), "setspawn");
        registerCommand(new SpawnCommand(this), "spawn");
        registerCommand(new GodCommand(this), "god");
        registerCommand(new TPACommand(this), "tpa");
        registerCommand(new TPAAcceptCommand(this), "tpaaccept");
        registerCommand(new TPADenyCommand(this), "tpadeny");
        registerCommand(new TPAToggleCommand(this), "tpatoggle");
        registerCommand(new SkullCommand(this), "skull");
        registerCommand(new MaintenanceCommand(this), "maintenance");
        registerCommand(new SocialSpyCommand(this), "spy");
        registerCommand(new ConvertCommand(this), "convert");
        registerCommand(new RulesCommand(this), "rules");
        registerCommand(new AFKCommand(this), "afk");

        if (!disabledCommands.isEmpty()) {
            logger.info(ChatColor.RED + "Some commands are disabled. You have to remove them from disabled commands list and restart the server if you want to use them. ");
            logger.info(ChatColor.RED + "Disabled commands:");
            disabledCommands.forEach(cmd -> logger.info(ChatColor.DARK_AQUA + "- " + cmd));
        }
    }

    private final Set<String> disabledCommands = new HashSet<>();

    public void registerCommand(CommandExecutor executor, String command) {
        String className = executor.getClass().getName();
        int beginIndex = className.lastIndexOf(".") + 1;
        className = className.substring(beginIndex).trim();
        if (!getConfig().getStringList("disabled-commands").contains(className)) {
            getCommand(command).setExecutor(executor);
        } else {
            disabledCommands.add(className);
        }
    }

    public Set<String> getDisabledCommands() {
        return disabledCommands;
    }

    private void checkUpdate() {

        if (!getConfig().getBoolean("updates.notify", true)) {
            return;
        }

        UpdateChecker.init(this, 92388).requestUpdateCheck().whenComplete((result, exception) -> {

            if (!result.requiresUpdate()) {
                return;
            }

            if (result.getNewestVersion().contains("b")) {
                if (getConfig().getBoolean("updates.notify-beta-versions", true)) {
                    logger.info(ChatColor.GREEN + "Found a new beta version available: v" + result.getNewestVersion());
                    logger.info(ChatColor.GREEN + "Download it on SpigotMC:");
                    logger.info(ChatColor.BLUE + "https://www.spigotmc.org/resources/simpleservertools-1-8-8-1-16-5.92388/");
                }
                return;
            }

            logger.info(ChatColor.GREEN + "Found a new version available: v" + result.getNewestVersion());
            logger.info(ChatColor.GREEN + "Download it SpigotMC:");
            logger.info(ChatColor.BLUE + "https://www.spigotmc.org/resources/simpleservertools-1-8-8-1-16-5.92388/");

        });

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

    public LanguageManager getLanguageManager() {
        return languageManager;
    }

    public SpyManager getSpyManager() {
        return spyManager;
    }

    public ConverterManager getConverterManager() {
        return converterManager;
    }

    public boolean isConverterManagerReady() {
        return converterManager != null;
    }

    public RulesManager getRulesManager() {
        return rulesManager;
    }

    public void checkAndLoadPacketListener() {
        if (pluginManager.isPluginEnabled("ProtocolLib")) {
            PLibManager.loadPacketListener(this);
        }
    }

    public void reload(boolean first) {
        long start = System.currentTimeMillis();
        logger.info(ChatColor.GREEN + "Reloading configurations from disk...");
        reloadConfig();
        checkAndLoadPacketListener();
        placeholderManager.load();
        if (Utils.getBoolean("convert-enabled")) {
            converterManager = new ConverterManager(this);
        } else {
            converterManager = null;
        }
        if (!first) {
            languageManager.loadLanguage();
            announcementManager.reload();
            warpManager.reloadWarps();
            warpManager.loadSettings();
            spawnManager.reloadSpawn();
            joinMessageManager.reload();
            tpaManager.loadSettings();
            maintenanceManager.reload();
            rulesManager.reloadRules();
            if (Utils.getBoolean("auto-save-users")) {
                asyncUserSaveThread = new AsyncUserSaveThread(this);
            } else if (asyncUserSaveThread != null) {
                asyncUserSaveThread.cancel();
            }
            logger.log(Level.INFO, ChatColor.GREEN + "Reloading is done, enjoy! " + ChatColor.AQUA + "{0} ms", System.currentTimeMillis() - start);
        }
    }

}
