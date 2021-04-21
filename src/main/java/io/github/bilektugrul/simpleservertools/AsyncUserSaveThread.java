package io.github.bilektugrul.simpleservertools;

import io.github.bilektugrul.simpleservertools.users.UserManager;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.logging.Logger;

public class AsyncUserSaveThread extends BukkitRunnable {

    private final SST plugin;
    private final Logger logger;
    private final UserManager userManager;

    public AsyncUserSaveThread(SST plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.userManager = plugin.getUserManager();
        start();
    }

    public void start() {
        logger.info("Async oyuncu kayıt süreci başlatılıyor...");
        int i = plugin.getConfig().getInt("auto-save-interval");
        runTaskTimerAsynchronously(plugin, 2400, (i * 60L) * 20);
    }

    @Override
    public void run() {
        try {
            userManager.saveUsers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
