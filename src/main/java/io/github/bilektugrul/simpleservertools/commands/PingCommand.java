package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PingCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("sst.ping")) {
            Utils.noPermission(sender);
            return true;
        }

        Player pingPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        if (pingPlayer == null) {
            sender.sendMessage(Utils.getMessage("ping.not-found", sender));
            return true;
        }

        if (pingPlayer.equals(sender)) {
            pingPlayer.sendMessage(Utils.getMessage("ping.message", pingPlayer)
                    .replace("%ping%", Integer.toString(Utils.getPing(pingPlayer))));
        } else {
            sender.sendMessage(Utils.getMessage("ping.message-other", sender)
                    .replace("%ping%", Integer.toString(Utils.getPing(pingPlayer))));
        }
        return true;
    }

}
