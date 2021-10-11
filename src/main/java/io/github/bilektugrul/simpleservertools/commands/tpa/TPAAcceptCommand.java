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
        if (sender instanceof Player p) {
            if (!p.hasPermission("sst.tpa")) {
                Utils.noPermission(p);
                return true;
            }

            if (args.length == 0) {
                p.sendMessage(Utils.getMessage("tpa.usage-accept", p));
                return true;
            }

            if (!tpaManager.isPresent(p)) {
                p.sendMessage(Utils.getMessage("tpa.no-request", p));
                return true;
            }

            Player reqSender = Bukkit.getPlayer(args[0]);
            if (reqSender == null) {
                p.sendMessage(Utils.getMessage("tpa.player-not-found", p));
                return true;
            }

            if (reqSender.equals(p)) {
                p.sendMessage(Utils.getMessage("tpa.not-yourself", p));
                return true;
            }

            if (!tpaManager.isPresent(p, reqSender)) {
                p.sendMessage(Utils.getMessage("tpa.no-request-from", p));
                return true;
            }

            if (!userManager.getUser(reqSender).isAvailable()) {
                p.sendMessage(Utils.getMessage("tpa.not-available", p));
                return true;
            }

            TPAInfo info = new TPAInfo(reqSender, p);
            TeleportMode mode = new TeleportMode(Mode.TPA, info);
            reqSender.sendMessage(Utils.getMessage("tpa.request-accepted", reqSender)
                    .replace("%teleporting%", p.getName()));
            p.sendMessage(Utils.getMessage("tpa.request-accepted-2", p)
                    .replace("%requester%", reqSender.getName()));
            tpaManager.teleport(reqSender, p, p.getLocation(), mode);

        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}