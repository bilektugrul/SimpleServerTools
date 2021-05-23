package io.github.bilektugrul.simpleservertools.features.maintenance;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

public class MaintenanceManager {

    private FileConfiguration maintenanceFile;

    private String reason;
    private boolean isInMaintenance;

    private final SST plugin;

    public MaintenanceManager(SST plugin) {
        this.plugin = plugin;
        reload();
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    public void toggleMaintenance() {
        isInMaintenance = !isInMaintenance;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void reload() {
        maintenanceFile = ConfigUtils.getConfig(plugin, "maintenance");
        isInMaintenance = maintenanceFile.getBoolean("maintenance.in-maintenance");
        String lastReason = Utils.getString(maintenanceFile, "maintenance.last-reason", null, false);
        reason = lastReason.isEmpty()
                ? Utils.getString(maintenanceFile, "maintenance.default-reason", null)
                : lastReason;
    }

    public void save() {
        maintenanceFile.set("maintenance.last-reason", reason);
        maintenanceFile.set("maintenance.in-maintenance", isInMaintenance);
        ConfigUtils.saveConfig(plugin, maintenanceFile, "maintenance");
    }

    public FileConfiguration getMaintenanceFile() {
        return maintenanceFile;
    }

}
