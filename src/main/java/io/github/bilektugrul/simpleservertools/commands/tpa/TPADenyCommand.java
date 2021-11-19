package io.github.bilektugrul.simpleservertools.commands.tpa;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPADenyCommand implements CommandExecutor {

    private final TPAManager tpaManager;

    public TPADenyCommand(SST plugin) {
        this.tpaManager = plugin.getTPAManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player senderPlayer) || !senderPlayer.hasPermission("sst.tpa")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.usage-deny", senderPlayer));
            return true;
        }

        if (!tpaManager.isPresent(senderPlayer)) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.no-request", senderPlayer));
            return true;
        }

        Player reqSender = Bukkit.getPlayer(args[0]);
        if (reqSender == null) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.player-not-found", senderPlayer));
            return true;
        }

        if (reqSender.equals(senderPlayer)) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.not-yourself", senderPlayer));
            return true;
        }

        if (!tpaManager.isPresent(senderPlayer, reqSender)) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.no-request-from", senderPlayer));
            return true;
        }

        tpaManager.remove(reqSender, senderPlayer);
        reqSender.sendMessage(Utils.getMessage("tpa.request-denied", reqSender)
                .replace("%teleporting%", senderPlayer.getName()));
        senderPlayer.sendMessage(Utils.getMessage("tpa.request-denied-2", senderPlayer)
                .replace("%requester%", reqSender.getName()));
        return true;
    }
}