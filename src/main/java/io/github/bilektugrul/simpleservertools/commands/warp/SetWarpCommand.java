package io.github.bilektugrul.simpleservertools.commands.warp;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.WeakHashMap;

public class SetWarpCommand implements CommandExecutor {

    private final WarpManager warpManager;

    public SetWarpCommand(SST plugin) {
        this.warpManager = plugin.getWarpManager();
    }

    private final WeakHashMap<String, String> forceCreateList = new WeakHashMap<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player) || !sender.hasPermission("sst.admin")) {
            sender.sendMessage(Utils.getString("no-permission", sender));
            return true;
        }

        Player p = (Player) sender;
        Location loc = p.getLocation();

        if (args.length == 0) {
            p.sendMessage(Utils.getString("other-messages.warps.not-enough-arguments", p));
            return true;
        }

        String arg = args[0];
        boolean force = forceCreateList.containsValue(arg);

        if (!force && warpManager.isPresent(arg)) {
            sender.sendMessage(Utils.getString("other-messages.warps.already-exists", sender));
            forceCreateList.put(sender.getName(), arg);
            return true;
        }

        boolean permRequired = args.length == 2;

        if (force) {
            warpManager.forceRegisterWarp(arg, loc, permRequired);
            p.sendMessage(Utils.getString("other-messages.warps.created", p)
                    .replace("%warp%", arg));
        } else if (warpManager.registerWarp(arg, loc, permRequired)) {
            p.sendMessage(Utils.getString("other-messages.warps.created", p)
                    .replace("%warp%", arg));
        } else {
            p.sendMessage(Utils.getString("other-messages.wrong-usage", p));
        }
        return true;
    }

}