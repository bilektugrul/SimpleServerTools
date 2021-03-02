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
        if (sender.hasPermission("sst.kick")) {
            if (args.length >= 1) {
                Player kickPlayer = Bukkit.getPlayer(args[0]);
                if (kickPlayer != null && !kickPlayer.hasPermission("sst.admin") || sender.hasPermission("sst.kick.admins")) {
                    String kickString = null;
                    if (args.length >= 2) {
                        String[] kickMsg = Arrays.copyOfRange(args, 1, args.length);
                        kickString = Utils.colorMessage(String.join(" ", kickMsg));
                    }
                    if (kickString == null) kickString = Utils.getString("other-messages.kick.kick-message", kickPlayer)
                            .replace("%kicker%", sender.getName());
                    kickPlayer.kickPlayer(kickString);
                    if (Utils.getBoolean("other-messages.kick.broadcast.enabled")) {
                        String kickBc = Utils.getString("other-messages.kick.broadcast.message", kickPlayer)
                                .replace("%kicked%", kickPlayer.getName())
                                .replace("%kicker%", sender.getName());
                        Bukkit.broadcastMessage(kickBc);
                    }
                    sender.sendMessage(Utils.getString("other-messages.kick.kicked", sender)
                            .replace("%kicked%", kickPlayer.getName()));
                } else {
                    sender.sendMessage(Utils.getString("other-messages.kick.went-wrong", sender));
                }
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

}
