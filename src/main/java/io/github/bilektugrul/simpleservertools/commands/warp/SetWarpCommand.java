package io.github.bilektugrul.simpleservertools.commands.warp;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

public class SetWarpCommand implements CommandExecutor {

    private final SST plugin;
    private final WarpManager warpManager;

    public SetWarpCommand(SST plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
    }

    private final WeakHashMap<String, String> forceCreateList = new WeakHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) || !sender.hasPermission("sst.admin")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        Player p = (Player) sender;
        String name = p.getName();
        Location loc = p.getLocation();

        if (args.length == 0) {
            p.sendMessage(Utils.getMessage("warps.not-enough-arguments", p));
            return true;
        }

        String arg = args[0];
        boolean force = forceCreateList.containsValue(arg);

        if (!force && warpManager.isPresent(arg)) {
            sender.sendMessage(Utils.getMessage("warps.already-exists", sender));
            forceCreateList.put(name, arg);
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> forceCreateList.remove(name), 100);
            return true;
        }

        boolean permRequired = args.length == 2;

        if (force) {
            warpManager.forceRegisterWarp(arg, loc, permRequired);
            p.sendMessage(Utils.getMessage("warps.created", p)
                    .replace("%warp%", arg));
            forceCreateList.remove(name);
        } else if (warpManager.registerWarp(arg, loc, permRequired)) {
            p.sendMessage(Utils.getMessage("warps.created", p)
                    .replace("%warp%", arg));
        } else {
            p.sendMessage(Utils.getMessage("wrong-usage", p));
        }
        return true;
    }

}