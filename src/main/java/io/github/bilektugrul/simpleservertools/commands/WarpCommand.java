package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.stuff.ActionBar;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.warps.Warp;
import io.github.bilektugrul.simpleservertools.warps.WarpManager;
import me.despical.commonsbox.compat.Titles;
import me.despical.commonsbox.miscellaneous.MiscUtils;
import me.despical.commonsbox.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

public class WarpCommand implements CommandExecutor {

    private class WarpTabCompleter implements TabCompleter {

        @Nullable
        @Override
        public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
            return warpManager.getWarpList().stream().map(Warp::getName).collect(Collectors.toList());
        }
    }

    private final SimpleServerTools plugin;
    private final WarpManager warpManager;

    public WarpCommand(SimpleServerTools plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
        plugin.getCommand("warp").setTabCompleter(new WarpTabCompleter());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("list")) {
                if (sender.hasPermission(SimpleServerTools.listPerm)) {
                    sender.sendMessage(Utils.getString("other-messages.warps.list")
                            .replace("%warpamount%", String.valueOf(warpManager.getWarpList().size()))
                            .replace("%warps%", warpManager.readableWarpList()));
                } else {
                    if (sender instanceof Player) {
                        sender.sendMessage(Utils.getString("no-permission", (Player) sender));
                    } else {
                        sender.sendMessage(Utils.getPAPILessString("other-messages.config-reloaded")
                                .replace("%player%", "CONSOLE"));
                    }
                }
            } else if (sender instanceof Player) {
                Player p = (Player) sender;
                String arg = args[0];
                if (arg.equalsIgnoreCase("save")) {
                    warpManager.saveWarps();
                    MiscUtils.sendCenteredMessage(p, Utils.getString("other-messages.warps.saved", p));
                } else if (warpManager.isPresent(arg)) {

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
                                    p.sendMessage(Utils.getString("other-messages.warps.info")
                                            .replace("%warp%", arg)
                                            .replace("%warpinfo%", LocationSerializer.locationToString(warp.getLocation())));
                                    return true;
                            }
                        }

                    } else if (args.length == 1) {
                        Warp warp = warpManager.getWarp(arg);
                        if (warp.getPermRequire() && p.hasPermission(warp.getPermission())) {
                            teleport(p, warp);
                        } else if (!warp.getPermRequire()) {
                            teleport(p, warp);
                        } else {
                            p.sendMessage(Utils.getString("other-messages.warps.no-permission", p));
                        }
                    }

                } else if (p.hasPermission(SimpleServerTools.adminPerm)) {
                    if (args.length == 2) {
                        if (warpManager.registerWarp(arg, p.getLocation(), args[1])) {
                            p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                    .replace("%warp%", arg));
                        }
                    } else if (args.length == 1) {
                        if (warpManager.registerWarp(arg, p.getLocation())) {
                            p.sendMessage(Utils.getString("other-messages.warps.created", p)
                                    .replace("%warp%", arg));
                        }
                    }
                }
            } else {
                plugin.sendMessage(sender, "help-message");
            }
        }
        return true;
    }

    public void teleport(Player p, Warp warp) {
        new BukkitRunnable() {
            int time = Utils.getInt("warps.teleport-time");
            final Location loc = warp.getLocation();
            final String warpName = warp.getName();

            String teleportingMsg = Utils.getString("other-messages.warps.teleporting.message", p).replace("%warp%", warpName);
            final String teleportingMode = Utils.getPAPILessString("other-messages.warps.teleporting.mode");
            String teleportingSubtitle = "";

            String teleportedMsg = Utils.getString("other-messages.warps.teleported.message", p).replace("%warp%", warpName);
            final String teleportedMode = Utils.getPAPILessString("other-messages.warps.teleported.mode");
            String teleportedSubtitle  = "";

            {
                if (teleportingMode.equalsIgnoreCase("TITLE") && teleportingMsg.contains("\n")) {
                    String[] title = teleportingMsg.split("\n");
                    teleportingSubtitle = title[1];
                    teleportingMsg = title[0];
                } if (teleportedMode.equalsIgnoreCase("TITLE") && teleportedMsg.contains("\n")) {
                    String[] title = teleportedMsg.split("\n");
                    teleportedSubtitle = title[1];
                    teleportedMsg = title[0];
                }
            }

            @Override
            public void run() {
                if (time == 0) {
                    p.teleport(loc);
                    this.cancel();
                    switch (teleportedMode) {
                        case "CHAT":
                            p.sendMessage(teleportedMsg);
                            break;
                        case "TITLE":
                            Titles.sendTitle(p, teleportedMsg, teleportedSubtitle);
                            break;
                        case "ACTIONBAR":
                            ActionBar.sendActionBar(p, teleportedMsg);
                            break;
                    }
                    return;
                } switch (teleportingMode) {
                    case "CHAT":
                        p.sendMessage(teleportingMsg.replace("%time%", String.valueOf(time)));
                        break;
                    case "TITLE":
                        Titles.sendTitle(p, teleportingMsg.replace("%time%", String.valueOf(time)), teleportingSubtitle.replace("%time%", String.valueOf(time)));
                        break;
                    case "ACTIONBAR":
                        ActionBar.sendActionBar(p, teleportingMsg.replace("%time%", String.valueOf(time)));
                        break;
                }
                this.time--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

}
