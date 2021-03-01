package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SSTCommand implements CommandExecutor {

    private final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("sst.admin")) {
                plugin.reload(false);
                sender.sendMessage(Utils.getString("other-messages.config-reloaded", sender));
            } else if (args.length >= 2 && args[1].equalsIgnoreCase("warpArgs")) {
                plugin.sendMessage(sender, "help-message.other.warpArgs.list");
            } else if (args[0].equalsIgnoreCase("help")) {
                plugin.sendMessage(sender, "help-message");
            }
        } else {
            plugin.sendMessage(sender, "help-message");
        }
        return true;
    }

}

