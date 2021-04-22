package io.github.bilektugrul.simpleservertools.features.maintenance;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class MaintenanceManager {

    private String reason;
    public boolean inMaintenance;

    private final SST plugin;

    public MaintenanceManager(SST plugin) {
        this.plugin = plugin;
        reload();
    }

    public void setInMaintenance(boolean inMaintenance) {
        this.inMaintenance = inMaintenance;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void reload() {
        inMaintenance = plugin.getConfig().getBoolean("maintenance.in-maintenance");
        String lastReason = Utils.getString("maintenance.last-reason", null, false);
        reason = lastReason.isEmpty()
                ? Utils.getString("maintenance.default-reason", null)
                : lastReason;
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("maintenance.last-reason", reason);
        config.set("maintenance.in-maintenance", inMaintenance);
    }

}
