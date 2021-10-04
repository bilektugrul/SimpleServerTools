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
    private final Set<TeleportTask> teleportTasks = new HashSet<>();

    public TeleportManager(SST plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
    }

    public void teleport(Player p, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        if (!userManager.isTeleporting(p)) {
            TeleportTask task = new TeleportTask(plugin, p, loc, teleportMode, teleportSettings);
            teleportTasks.add(task);
            task.runTaskTimer(plugin, 0, 20L);
        }
    }

    public void removeTeleportTask(TeleportTask task) {
        teleportTasks.remove(task);
    }

    public Set<TeleportTask> getTeleportTasks() {
        return new HashSet<>(teleportTasks);
    }

}
