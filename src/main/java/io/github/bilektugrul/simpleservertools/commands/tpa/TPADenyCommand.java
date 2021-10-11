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
        if (sender instanceof Player p) {
            if (!sender.hasPermission("sst.tpa")) {
                Utils.noPermission(sender);
                return true;
            }

            if (args.length == 0) {
                p.sendMessage(Utils.getMessage("tpa.usage-deny", p));
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

            tpaManager.remove(reqSender, p);
            reqSender.sendMessage(Utils.getMessage("tpa.request-denied", reqSender)
                    .replace("%teleporting%", p.getName()));
            p.sendMessage(Utils.getMessage("tpa.request-denied-2", p)
                    .replace("%requester%", reqSender.getName()));

        } else {
            Utils.noPermission(sender);
        }
        return true;
    }
}