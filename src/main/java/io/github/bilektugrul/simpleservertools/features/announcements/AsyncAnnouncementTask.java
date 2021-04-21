package io.github.bilektugrul.simpleservertools.features.announcements;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class AsyncAnnouncementTask extends BukkitRunnable {

    private final AnnouncementManager announcementManager;
    private VaultManager vaultManager = null;
    private final SST plugin;
    private final AnnouncementMode mode;
    private int next = 0;

    public boolean hasStarted = false;

    private final List<Announcement> list;

    public AsyncAnnouncementTask(AnnouncementManager announcementManager, SST plugin, AnnouncementMode mode) {
        this.announcementManager = announcementManager;
        if (plugin.isPermManagerReady()) {
            this.vaultManager = plugin.getVaultManager();
        } else {
            plugin.getLogger().warning("Vault bulunamadı. Grup bazlı duyurular çalışmayacak.");
        }
        this.plugin = plugin;
        this.mode = mode;
        this.list = announcementManager.getAnnouncements();
    }

    public void start() {
        plugin.getLogger().info("Async duyuru süreci başlatılıyor...");
        hasStarted = true;
        int i = announcementManager.getAnnouncementsFile().getInt("announcements.time");
        long time = i * 20L;
        runTaskTimerAsynchronously(plugin, time, time);
    }

    @Override
    public void run() {
        if (mode == AnnouncementMode.ORDERED) {
            announce(list.get(next++));
            if (next >= list.size()) {
                next = 0;
            }
        } else {
            int random = new Random().nextInt(list.size());
            announce(list.get(random));
        }
    }

    public void announce(Announcement announcement) {
        String content = announcement.getContent();
        AnnouncementType type = announcement.getType();
        if (type == AnnouncementType.NONE) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendMessage(Utils.replacePlaceholders(content, p));
            }
        } else if (type == AnnouncementType.GROUP && plugin.isPermManagerReady()) {
            Permission provider = vaultManager.getPermissionProvider();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Arrays.stream(provider.getPlayerGroups(p)).anyMatch(announcement.getGroup()::equalsIgnoreCase)) {
                    p.sendMessage(Utils.replacePlaceholders(content, p));
                }
            }
        } else {
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (p.hasPermission(announcement.getPermission())) {
                    p.sendMessage(Utils.replacePlaceholders(content, p));
                }
            }
        }
    }

}
