package io.github.bilektugrul.simpleservertools.commands.tpa;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAInfo;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.Mode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAAcceptCommand implements CommandExecutor {

    private final TPAManager tpaManager;
    private final UserManager userManager;

    public TPAAcceptCommand(SST plugin) {
        this.tpaManager = plugin.getTPAManager();
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player senderPlayer) || !senderPlayer.hasPermission("sst.tpa")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.usage-accept", senderPlayer));
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

        if (!userManager.getUser(reqSender).isAvailable()) {
            senderPlayer.sendMessage(Utils.getMessage("tpa.not-available", senderPlayer));
            return true;
        }

        TPAInfo info = new TPAInfo(reqSender, senderPlayer);
        TeleportMode mode = new TeleportMode(Mode.TPA, info);
        reqSender.sendMessage(Utils.getMessage("tpa.request-accepted", reqSender)
                .replace("%teleporting%", senderPlayer.getName()));
        senderPlayer.sendMessage(Utils.getMessage("tpa.request-accepted-2", senderPlayer)
                .replace("%requester%", reqSender.getName()));
        tpaManager.teleport(reqSender, senderPlayer, senderPlayer.getLocation(), mode);
        return true;
    }

}