package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPHereCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.tphere")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("tphere.wrong-usage", sender));
            return true;
        }

        Player senderPlayer = sender instanceof Player ? (Player) sender : null;
        if (senderPlayer == null) {
            Utils.noPermission(sender);
            return true;
        }

        Player toTeleport = Bukkit.getPlayer(args[0]);

        if (toTeleport == null) {
            sender.sendMessage(Utils.getMessage("tphere.type-player", sender));
            return true;
        }

        toTeleport.teleport(senderPlayer);
        return true;
    }

}