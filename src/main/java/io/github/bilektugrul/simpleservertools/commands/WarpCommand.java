package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.utils.Utils;
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

    public WarpCommand(SimpleServerTools plugin) {
        this.warpManager = plugin.getWarpManager();
        this.teleportManager = plugin.getTeleportManager();
        plugin.getCommand("warp").setTabCompleter(new WarpTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        boolean isAdmin = sender.hasPermission("sst.admin");
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission("sst.warplist")) {
                    sender.sendMessage(Utils.getString("other-messages.warps.list", sender)
                            .replace("%warpamount%", String.valueOf(warpManager.getWarpList().size()))
                            .replace("%warps%", warpManager.readableWarpList()));
                } else {
                    sender.sendMessage(Utils.getString("no-permission", sender));
                }
            } else if (args[0].equalsIgnoreCase("save") && isAdmin) {
                warpManager.saveWarps();
                sender.sendMessage(Utils.getString("other-messages.warps.saved", sender));
            } else if (isPlayer) {
                Player p = (Player) sender;
                String arg = args[0];
                if (warpManager.isPresent(arg)) {
                    if (args.length == 2) {
                        if (warpManager.isPresent(arg)) {
                            Warp warp = warpManager.getWarp(arg);
                            switch (args[1]) {
                                case "--del":
                                    if (isAdmin) {
                                        p.performCommand("warp " + arg + " --info");
                                        p.sendMessage(Utils.getString("other-messages.warps.deleting", p)
                                                .replace("%warp%", arg));
                                        warpManager.deleteWarp(arg);
                                        p.sendMessage(Utils.getString("other-messages.warps.deleted", p)
                                                .replace("%warp%", arg));
                                    }
                                    return true;
                                case "--force":
                                    if (isAdmin) {
                                        warpManager.forceRegisterWarp(arg, p.getLocation());
                                        p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                                .replace("%warp%", arg));
                                    }
                                    return true;
                                case "--info":
                                    if (!p.hasPermission("sst.warpinfo")) return true;
                                    p.sendMessage(Utils.getString("other-messages.warps.info", sender)
                                            .replace("%warp%", arg)
                                            .replace("%warploc%", warpManager.readableWarpLoc(warp))
                                            .replace("%warpperm%", warp.getPermRequire() ? warp.getPermission() : "yok"));
                                    return true;
                            }
                        }

                    } else if (args.length == 1) {
                        Warp warp = warpManager.getWarp(arg);
                        Location loc = warp.getLocation();
                        TeleportMode mode = new TeleportMode(TeleportMode.Mode.WARPS, warp, null, null);
                        if (!warp.getPermRequire() || warp.getPermRequire() && p.hasPermission(warp.getPermission())) {
                            teleportManager.teleport(p, loc, mode, warpManager.getSettings());
                        } else {
                            p.sendMessage(Utils.getString("other-messages.warps.no-permission", p));
                        }
                    }

                } else if (isAdmin) {
                    if (args.length == 2 && warpManager.registerWarp(arg, p.getLocation(), true)) {
                        p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                .replace("%warp%", arg));
                    } else if (args.length == 1 && warpManager.registerWarp(arg, p.getLocation())) {
                        p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                .replace("%warp%", arg));
                    } else {
                        p.sendMessage(Utils.getString("other-messages.wrong-usage", p));
                    }
                }
            } else {
                Utils.sendMessage(sender, "help-message");
            }
        }
        return true;
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
