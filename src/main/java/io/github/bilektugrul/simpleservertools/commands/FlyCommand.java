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
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        Player flightPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        if (flightPlayer != null) {
            boolean newFlightMode = args.length >= 2 ? Utils.matchMode(args[1]) : !flightPlayer.getAllowFlight();
            change(sender, flightPlayer, newFlightMode);
        } else {
            sender.sendMessage(Utils.getMessage("fly.type-player", sender));
        }
        return true;
    }

    public void change(CommandSender from, Player flightPlayer, boolean newMode) {

        boolean isSame = from.equals(flightPlayer);

        if (!isSame && !from.hasPermission("sst.fly.others")) {
            from.sendMessage(Utils.getMessage("no-permission", from));
            return;
        }

        flightPlayer.setAllowFlight(newMode);
        flightPlayer.setFlying(newMode);
        flightPlayer.sendMessage(Utils.getMessage("fly.toggled", flightPlayer)
                .replace("%flymode%", Utils.getMessage("fly.modes." + newMode, from)));
        if (!isSame) {
            from.sendMessage(Utils.getMessage("fly.toggled-other", from)
                    .replace("%other%", flightPlayer.getName())
                    .replace("%flymode%", Utils.getMessage("fly.modes." + newMode, from)));
        }
    }

}