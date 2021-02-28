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
        if (sender.hasPermission("sst.broadcast")) {
            if (args.length >= 1) {
                StringBuilder builder = new StringBuilder();
                builder.append(Utils.getString("other-messages.broadcast.prefix", sender)).append(" ");
                for (String arg : args) {
                    builder.append(arg).append(" ");
                }
                Bukkit.broadcastMessage(Utils.colorMessage(builder.toString()));
            } else {
                sender.sendMessage(Utils.getString("other-messages.broadcast.not-enough", sender));
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

}
