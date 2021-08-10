package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.spy.SpyManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SocialSpyCommand implements CommandExecutor {

    private final SpyManager spyManager;

    public SocialSpyCommand(SST plugin) {
        this.spyManager = plugin.getSpyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.socialspy")) {
            Utils.noPermission(sender);
            return true;
        }

        boolean argsPresent = args.length >= 1;

        if (argsPresent && args[0].equals("list")) {
            if (sender.hasPermission("sst.socialspy.list")) sender.sendMessage(Utils.getMessage("spy.list", sender)
                    .replace("%list%", spyManager.getReadableSpyList()));
            else Utils.noPermission(sender);
            return true;
        }

        Player toChange = argsPresent ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        if (toChange == null) {
            sender.sendMessage(Utils.getMessage("spy.not-online", sender));
            return true;
        }

        spyManager.toggleSpy(toChange, sender);
        return true;
    }

}
