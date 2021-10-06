package io.github.bilektugrul.simpleservertools.listeners;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.HomeManager;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import org.bukkit.event.Listener;

//TODO: separate listeners to multiple classes
public class ListenerAdapter implements Listener {

    protected final SST plugin;
    protected final UserManager userManager;
    protected final SpawnManager spawnManager;
    protected final WarpManager warpManager;
    protected final JoinMessageManager joinMessageManager;
    protected final VaultManager vaultManager;
    protected final VanishManager vanishManager;
    protected final TPAManager tpaManager;
    protected final MaintenanceManager maintenanceManager;
    protected final HomeManager homeManager;

    public ListenerAdapter(SST plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.spawnManager = plugin.getSpawnManager();
        this.warpManager = plugin.getWarpManager();
        this.joinMessageManager = plugin.getJoinMessageManager();
        this.vaultManager = plugin.getVaultManager();
        this.vanishManager = plugin.getVanishManager();
        this.tpaManager = plugin.getTPAManager();
        this.maintenanceManager = plugin.getMaintenanceManager();
        this.homeManager = plugin.getHomeManager();
    }

}