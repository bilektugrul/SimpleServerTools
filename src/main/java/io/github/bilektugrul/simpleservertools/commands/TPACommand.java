package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAInfo;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPACommand implements CommandExecutor {

    private final SimpleServerTools plugin;
    private final TPAManager tpaManager;
    private final UserManager userManager;
    private final TeleportManager teleportManager;

    public TPACommand(SimpleServerTools plugin) {
        this.plugin = plugin;
        this.tpaManager = plugin.getTPAManager();
        this.userManager = plugin.getUserManager();
        this.teleportManager = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.tpa") && sender instanceof Player) {
            Player p = (Player) sender;
            if (args.length == 1) {
                Player toTeleport = Bukkit.getPlayer(args[0]);
                if (toTeleport != null && !toTeleport.equals(sender)) {
                    TPAInfo info = new TPAInfo(p, toTeleport);
                    TeleportMode mode = new TeleportMode(TeleportMode.Mode.TPA, null, null, info);
                    if (!tpaManager.isTeleporting(userManager.getUser(p.getUniqueId()))) {
                        teleportManager.teleport(p, toTeleport.getLocation(), mode, tpaManager.getSettings());
                    }
                }
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

}
