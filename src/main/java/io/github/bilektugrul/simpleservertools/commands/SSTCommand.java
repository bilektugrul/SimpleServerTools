package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SSTCommand implements CommandExecutor {

    private final SST plugin;

    public SSTCommand(SST plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        boolean isAdmin = sender.hasPermission("sst.admin");
        if (!Utils.getBoolean("main-command-perm-required") || sender.hasPermission("sst.maincmd")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("reload") && isAdmin) {
                    plugin.reload(false);
                    sender.sendMessage(Utils.getMessage("config-reloaded", sender));
                } else if (args[0].equalsIgnoreCase("save-users") && isAdmin) {
                    try {
                        plugin.getUserManager().saveUsers();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    sender.sendMessage(Utils.getMessage("users-saved", sender));
                } else if (args[0].equalsIgnoreCase("help")) {
                    Utils.sendMessage(sender, "help-message");
                }
            } else {
                Utils.sendMessage(sender, "help-message");
            }
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}