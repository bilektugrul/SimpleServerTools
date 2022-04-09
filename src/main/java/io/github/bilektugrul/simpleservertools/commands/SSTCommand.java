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
        if (Utils.getBoolean("main-command-perm-required") && !sender.hasPermission("sst.maincmd")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            Utils.sendMessage(sender, "help-message");
            return true;
        }

        if (!sender.hasPermission("sst.admin")) {
            Utils.noPermission(sender);
            return true;
        }

        if (sender.hasPermission("sst.admin")) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reload(false);
                sender.sendMessage(Utils.getMessage("config-reloaded", sender));
            } else if (args[0].equalsIgnoreCase("save-users")) {
                try {
                    plugin.getUserManager().saveUsers();
                    sender.sendMessage(Utils.getMessage("users-saved", sender));
                } catch (IOException e) {
                    e.printStackTrace();
                    sender.sendMessage(Utils.getMessage("went-wrong", sender));
                }
            }
        }
        return true;
    }

}