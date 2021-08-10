package io.github.bilektugrul.simpleservertools.commands.speed;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.speed")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("speed.wrong-usage", sender));
            return true;
        }

        SpeedUsage usage = args.length >= 2 ? SpeedUsage.ADVANCED : SpeedUsage.BASIC;

        new SpeedInfo().setExecutor(sender)
                .setPlayer(args.length == 3 ? Bukkit.getPlayer(args[2]) : sender instanceof Player ? (Player) sender : null)
                .setMode(matchMode(args[0])) // apply() method will take care of mode if this returns null
                .setSpeed(usage == SpeedUsage.BASIC ? args[0] : args[1])
                .apply();

        return true;
    }

    private SpeedMode matchMode(String s) {
        s = s.toLowerCase(Locale.ROOT);
        if (s.equals("walk")) {
            return SpeedMode.WALK;
        } else if (s.equals("fly")) {
            return SpeedMode.FLY;
        }
        return null;
    }

    public enum SpeedUsage {

        BASIC, ADVANCED

    }

}
