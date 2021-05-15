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
        if (sender instanceof Player && sender.hasPermission("sst.speed")) {
            Player player = (Player) sender;
            String wrongUsage = Utils.getMessage("speed.wrong-usage", player);
            if (args.length >= 1) {
                SpeedInfo info = new SpeedInfo(player);
                if (args.length == 1) {
                    try {
                        info.setSpeed(Float.parseFloat(args[0]));
                    } catch (NumberFormatException ignored) {
                        player.sendMessage(wrongUsage);
                        return true;
                    }
                    info.apply();
                } else {
                    SpeedMode mode = matchMode(args[0]);
                    if (mode != null) {
                        info.setMode(mode);
                        if (args.length == 3) {
                            Player otherPlayer = Bukkit.getPlayer(args[2]);
                            if (otherPlayer != null) info.setPlayer(otherPlayer);
                            else {
                                player.sendMessage(Utils.getMessage("speed.not-found", player));
                                return true;
                            }
                        }
                        try {
                            info.setSpeed(Float.parseFloat(args[1]));
                        } catch (NumberFormatException ignored) {
                            player.sendMessage(wrongUsage);
                            return true;
                        }
                        info.apply();
                    } else {
                        player.sendMessage(wrongUsage);
                    }
                }
            } else {
                player.sendMessage(wrongUsage);
            }
        } else {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
        }
        return true;
    }

    private SpeedMode matchMode(String s) {
        return s.toLowerCase(Locale.ROOT).contains("walk") ? SpeedMode.WALK : SpeedMode.FLY;
    }

}
