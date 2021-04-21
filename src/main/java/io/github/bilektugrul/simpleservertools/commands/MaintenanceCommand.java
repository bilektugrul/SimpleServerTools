package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

    private final MaintenanceManager maintenanceManager;

    public MaintenanceCommand(SST plugin) {
        this.maintenanceManager = plugin.getMaintenanceManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.maintenance.command")) {
            maintenanceManager.setInMaintenance(!maintenanceManager.inMaintenance);
            sender.sendMessage(Utils.getString("maintenance.changed", sender)
                    .replace("%newMode%", Utils.getString("maintenance.modes." + maintenanceManager.inMaintenance, sender)));
        }
        return true;
    }

}
