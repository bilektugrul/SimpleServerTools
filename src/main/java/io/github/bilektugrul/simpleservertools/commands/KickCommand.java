package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class KickCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.kick")) {
            sender.sendMessage(Utils.getMessage("messages.no-permission", sender));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("messages.kick.wrong-usage", sender));
            return true;
        }

        Player kickPlayer = Bukkit.getPlayer(args[0]);

        if (kickPlayer != null && !kickPlayer.hasPermission("sst.admin") || sender.hasPermission("sst.kick.admins")) {
            String kickString = Utils.getMessage("messages.kick.kick-message", kickPlayer)
                    .replace("%kicker%", sender.getName());

            if (args.length >= 2) {
                String[] kickMsg = Arrays.copyOfRange(args, 1, args.length);
                kickString = Utils.replacePlaceholders(String.join(" ", kickMsg), kickPlayer, true);
            }

            kickPlayer.kickPlayer(kickString);

            if (Utils.getBoolean("messages.kick.broadcast.enabled")) {
                String kickBc = Utils.getMessage("messages.kick.broadcast.message", kickPlayer)
                        .replace("%kicked%", kickPlayer.getName())
                        .replace("%kicker%", sender.getName());
                Bukkit.broadcastMessage(kickBc);
            }

            sender.sendMessage(Utils.getMessage("messages.kick.kicked", sender)
                    .replace("%kicked%", kickPlayer.getName()));
        } else {
            sender.sendMessage(Utils.getMessage("messages.kick.went-wrong", sender));
        }
        return true;
    }

}
