package io.github.bilektugrul.simpleservertools.commands.warp;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.Mode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor {

    private final WarpManager warpManager;
    private final TeleportManager teleportManager;

    public WarpCommand(SST plugin) {
        this.warpManager = plugin.getWarpManager();
        this.teleportManager = plugin.getTeleportManager();
        plugin.getCommand("warp").setTabCompleter(new WarpTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {

        boolean isPlayer = (sender instanceof Player);
        boolean isAdmin = sender.hasPermission("sst.admin");
        boolean canSeeList = isAdmin || sender.hasPermission("sst.warplist");

        if (args.length == 0) {
            if (canSeeList) {
                sendWarpList(sender);
            } else {
                sender.sendMessage(Utils.getMessage("messages.warps.wrong-usage", sender));
            }
            return true;
        }

        String arg = args[0];

        if (arg.equalsIgnoreCase("list")) {

            if (canSeeList) sendWarpList(sender);
            else sender.sendMessage(Utils.getMessage("messages.no-permission", sender));

        } else if (arg.equalsIgnoreCase("save") && isAdmin) {
            warpManager.saveWarps();
            sender.sendMessage(Utils.getMessage("messages.warps.saved", sender));

        } else if (isPlayer) {
            Player p = (Player) sender;
            if (warpManager.isPresent(arg)) {
                Warp warp = warpManager.getWarp(arg);
                Location loc = warp.getLocation();
                TeleportMode mode = new TeleportMode(Mode.WARPS, warp, null, null);
                if (!warp.getPermRequire() || warp.getPermRequire() && p.hasPermission(warp.getPermission())) {
                    teleportManager.teleport(p, loc, mode, warpManager.getSettings());
                } else {
                    p.sendMessage(Utils.getMessage("messages.warps.messages.no-permission", p));
                }
            } else {
                p.sendMessage(Utils.getMessage("messages.warps.do-not-exist", p));
            }
        } else {
            sender.sendMessage(Utils.getMessage("messages.warps.wrong-usage", sender));
        }
        return true;
    }

    private void sendWarpList(CommandSender sender) {
        sender.sendMessage(Utils.getMessage("messages.warps.list", sender)
                .replace("%warpamount%", String.valueOf(warpManager.getWarpList().size()))
                .replace("%warps%", warpManager.readableWarpList(sender)));
    }

    private class WarpTabCompleter implements TabCompleter {

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
            if (Utils.getBoolean("warps.tab-complete")) {
                boolean isAdmin = sender.hasPermission("sst.admin");
                switch (args.length) {
                    case 1:
                        return warpManager.getWarpList().stream()
                                .filter(warp -> isAdmin || !warp.getPermRequire() || warp.getPermRequire() && sender.hasPermission(warp.getPermission()))
                                .map(Warp::getName)
                                .collect(Collectors.toList());
                    case 2:
                        List<String> list = new ArrayList<>();
                        if (isAdmin) {
                            list.add("--del");
                            list.add("--force");
                            list.add("--info");
                        } else if (sender.hasPermission("sst.warpinfo")) {
                            list.add("--info");
                        }
                        return list;
                }
            }
            return Collections.emptyList();
        }

    }

}
