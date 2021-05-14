package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class BroadcastCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.broadcast")) {
            sender.sendMessage(Utils.getMessage("messages.no-permission", sender));
            return true;
        }

        if (args.length >= 1) {
            String builder = Utils.getMessage("messages.broadcast.prefix", sender) + " " +
                    Utils.arrayToString(args, sender, true, true);
            Bukkit.broadcastMessage(Utils.replacePlaceholders(builder, sender, true));
        } else {
            sender.sendMessage(Utils.getMessage("messages.broadcast.not-enough", sender));
        }
        return true;
    }

}
