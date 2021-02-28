package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(SimpleServerTools.vanishPerm)) {
                if (!Utils.isVanished(player.getUniqueId())) Utils.hidePlayer(player, false);
                else Utils.showPlayer(player, false);
            } else {
                player.sendMessage(Utils.getString("no-permission", player));
            }
        } else if (args.length >= 1) {
            Player toVanish = Bukkit.getPlayer(args[0]);
            if (toVanish != null) {
                if (!Utils.isVanished(toVanish.getUniqueId())) Utils.hidePlayer(toVanish, false);
                else Utils.showPlayer(toVanish, false);
                sender.sendMessage(Utils.getString("other-messages.vanish.toggled", sender)
                        .replace("%other%", toVanish.getName()));
            } else {
                sender.sendMessage(Utils.getString("other-messages.vanish.player-not-found", sender)
                        .replace("%other%", args[0]));
            }
        }
        return true;
    }

}
