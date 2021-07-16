package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TeleportManager {

    private final SST plugin;

    private final UserManager userManager;

    public TeleportManager(SST plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    private final Set<TeleportTask> teleportTasks = new HashSet<>();

    public Set<TeleportTask> getTeleportTasks() {
        return teleportTasks;
    }

    public void teleport(Player p, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        if (!userManager.isTeleporting(p)) {
            TeleportTask task = new TeleportTask(p, loc, teleportMode, teleportSettings, this, userManager);
            teleportTasks.add(task);
            task.runTaskTimer(plugin, 0, 20L);
        }
    }

}
