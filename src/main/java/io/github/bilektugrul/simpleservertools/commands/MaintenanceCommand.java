package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

public class MaintenanceCommand implements CommandExecutor {

    private final MaintenanceManager maintenanceManager;

    public MaintenanceCommand(SST plugin) {
        this.maintenanceManager = plugin.getMaintenanceManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.maintenance.command")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        FileConfiguration file = maintenanceManager.getMaintenanceFile();

        maintenanceManager.toggleMaintenance();
        String reason = maintenanceManager.getReason();

        if (args.length >= 1) {
            reason = Utils.arrayToString(args, sender, false, false);
        }

        if (maintenanceManager.isInMaintenance()) {
            maintenanceManager.setReason(reason);
            if (file.getBoolean("maintenance.kick-players.enabled")) {
                String finalReason = reason.trim();
                Bukkit.getOnlinePlayers().stream()
                        .filter(p -> !p.hasPermission("sst.maintenance.join"))
                        .forEach(p -> p.kickPlayer(Utils.getString(file, "maintenance.kick-players.kick-message", p)
                                .replace("%reason%", Utils.replacePlaceholders(finalReason, p, true))));
            }
        }

        sender.sendMessage(Utils.getString(file, "maintenance.changed", sender)
                .replace("%newmode%", Utils.getString(file, "maintenance.modes." + maintenanceManager.isInMaintenance(), sender))
                .replace("%reason%", reason));
        return true;
    }

}
