package io.github.bilektugrul.simpleservertools.features.maintenance;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.configuration.file.FileConfiguration;

public class MaintenanceManager {

    private String reason;
    public boolean inMaintenance;

    private final FileConfiguration config;

    public MaintenanceManager(SST plugin) {
        this.config = plugin.getConfig();
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
        inMaintenance = config.getBoolean("maintenance.in-maintenance");
        String lastReason = Utils.getString("maintenance.last-reason", null);
        reason = lastReason.isEmpty()
                ? Utils.getString("maintenance.default-reason", null)
                : lastReason;
    }

    public void save() {
        config.set("maintenance.last-reason", reason);
        config.set("maintenance.in-maintenance", inMaintenance);
    }

}
