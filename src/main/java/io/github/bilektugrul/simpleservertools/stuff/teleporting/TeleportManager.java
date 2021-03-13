package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public class TeleportManager {

    private final SimpleServerTools plugin;

    public TeleportManager(SimpleServerTools plugin) {
        this.plugin = plugin;
    }

    private final Set<TeleportTask> teleportTasks = new HashSet<>();

    public Set<TeleportTask> getTeleportTasks() {
        return teleportTasks;
    }

    public void teleport(Player p, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        TeleportTask task = new TeleportTask(p, loc, teleportMode, teleportSettings);
        teleportTasks.add(task);
        task.runTaskTimer(plugin, 0, 20L);
    }

}
