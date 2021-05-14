package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FlyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.fly")) {
            sender.sendMessage(Utils.getMessage("messages.no-permission", sender));
            return true;
        }

        Player flightPlayer = null;
        boolean argFlightMode = false;

        if (args.length >= 1) {
            flightPlayer = Bukkit.getPlayer(args[0]);
            if (args.length >= 2) {
                argFlightMode = true;
            }
        }

        if (flightPlayer == null && sender instanceof Player) flightPlayer = (Player) sender;
        if (flightPlayer != null) {
            if (argFlightMode) {
                boolean newMode = Utils.matchMode(args[1]);
                change(sender, flightPlayer, newMode);
            } else {
                change(sender, flightPlayer, !flightPlayer.getAllowFlight());
            }
        } else {
            sender.sendMessage(Utils.getMessage("messages.fly.type-player", sender));
        }
        return true;
    }

    public void change(CommandSender from, Player flightPlayer, boolean newMode) {
        flightPlayer.setAllowFlight(newMode);
        flightPlayer.setFlying(newMode);
        flightPlayer.sendMessage(Utils.getMessage("messages.fly.toggled", flightPlayer)
                .replace("%flymode%", Utils.getMessage("messages.fly.modes." + newMode, from)));
        if (!from.equals(flightPlayer)) {
            from.sendMessage(Utils.getMessage("messages.fly.toggled-other", from)
                    .replace("%other%", flightPlayer.getName())
                    .replace("%flymode%", Utils.getMessage("messages.fly.modes." + newMode, from)));
        }
    }

}