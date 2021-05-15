package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    private final VanishManager vanishManager;

    public VanishCommand(SST plugin) {
        this.vanishManager = plugin.getVanishManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 && sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission("sst.vanish")) {
                if (!vanishManager.isVanished(player.getUniqueId())) vanishManager.hidePlayer(player, false);
                else vanishManager.showPlayer(player, false);
            } else {
                player.sendMessage(Utils.getMessage("no-permission", player));
            }
        } else if (args.length >= 1 && sender.hasPermission("sst.vanish.others")) {
            Player toVanish = Bukkit.getPlayer(args[0]);
            if (toVanish != null) {
                if (!vanishManager.isVanished(toVanish.getUniqueId())) vanishManager.hidePlayer(toVanish, false);
                else vanishManager.showPlayer(toVanish, false);
                sender.sendMessage(Utils.getMessage("vanish.toggled", sender)
                        .replace("%other%", toVanish.getName()));
            } else {
                sender.sendMessage(Utils.getMessage("vanish.player-not-found", sender)
                        .replace("%other%", args[0]));
            }
        }
        return true;
    }

}
