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
        if (!sender.hasPermission("sst.vanish")) {
            Utils.noPermission(sender);
            return true;
        }

        Player vanishPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;
        boolean isSame = sender.equals(vanishPlayer);

        if (!isSame && !sender.hasPermission("sst.vanish.others")) {
            Utils.noPermission(sender);
            return true;
        }

        if (vanishPlayer == null) {
            sender.sendMessage(Utils.getMessage("vanish.player-not-found", sender));
            return true;
        }

        vanishManager.toggleVanish(vanishPlayer, false);

        if (!isSame) sender.sendMessage(Utils.getMessage("vanish.toggled", sender)
                    .replace("%other%", vanishPlayer.getName())
                    .replace("%mode%", vanishManager.modeString(vanishPlayer)));
        return true;
    }

}
