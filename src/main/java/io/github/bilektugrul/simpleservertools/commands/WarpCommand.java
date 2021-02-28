package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.stuff.TeleportMode;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor {

    private final SimpleServerTools plugin;
    private final WarpManager warpManager;

    public WarpCommand(SimpleServerTools plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
        plugin.getCommand("warp").setTabCompleter(new WarpTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission(SimpleServerTools.listPerm)) {
                    sender.sendMessage(Utils.getString("other-messages.warps.list", sender)
                            .replace("%warpamount%", String.valueOf(warpManager.getWarpList().size()))
                            .replace("%warps%", warpManager.readableWarpList()));
                } else if (isPlayer) {
                    sender.sendMessage(Utils.getString("no-permission", (Player) sender));
                } else {
                    sender.sendMessage(Utils.getString("other-messages.config-reloaded", sender)
                            .replace("%player%", "CONSOLE"));
                }
            } else if (args[0].equalsIgnoreCase("save") && sender.hasPermission(SimpleServerTools.adminPerm)) {
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
                                    warpManager.deleteWarp(arg);
                                    p.sendMessage(Utils.getString("other-messages.warps.deleted", p)
                                            .replace("%warp%", arg));
                                    return true;
                                case "--force":
                                    warpManager.forceRegisterWarp(arg, p.getLocation());
                                    p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                            .replace("%warp%", arg));
                                    return true;
                                case "--info":
                                    p.sendMessage(Utils.getString("other-messages.warps.info", sender)
                                            .replace("%warp%", arg)
                                            .replace("%warploc%", warpManager.readableWarpLoc(warp))
                                            .replace("%warpperm%", warp.getPermRequire() ? "sst.warps." + warp.getName() : "none"));
                                    return true;
                            }
                        }

                    } else if (args.length == 1) {
                        Warp warp = warpManager.getWarp(arg);
                        Location loc = warp.getLocation();
                        TeleportMode mode = new TeleportMode(TeleportMode.Mode.WARPS, warp, null);
                        if (warp.getPermRequire() && p.hasPermission("sst.warps." + warp.getName())) {
                            Utils.teleport(p, loc, mode);
                        } else if (!warp.getPermRequire()) {
                            Utils.teleport(p, loc, mode);
                        } else {
                            p.sendMessage(Utils.getString("other-messages.warps.no-permission", p));
                        }
                    }

                } else if (p.hasPermission(SimpleServerTools.adminPerm)) {
                    if (args.length == 2 && (warpManager.registerWarp(arg, p.getLocation(), true))) {
                        p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                .replace("%warp%", arg));
                    } else if (args.length == 1 && (warpManager.registerWarp(arg, p.getLocation()))) {
                        p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                .replace("%warp%", arg));
                    } else {
                        p.sendMessage(Utils.getString("other-messages.wrong-usage", p));
                    }
                }
            } else {
                plugin.sendMessage(sender, "help-message");
            }
        }
        return true;
    }


    private class WarpTabCompleter implements TabCompleter {

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            return warpManager.getWarpList().stream().map(Warp::getName).collect(Collectors.toList());
        }
    }

}
