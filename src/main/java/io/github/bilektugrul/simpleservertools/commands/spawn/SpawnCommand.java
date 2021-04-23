package io.github.bilektugrul.simpleservertools.commands.spawn;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.spawn.Spawn;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.Mode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;
    private final TeleportManager teleportManager;

    public SpawnCommand(SST plugin) {
        this.spawnManager = plugin.getSpawnManager();
        this.teleportManager = plugin.getTeleportManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender.hasPermission("sst.spawn")) {
            if (spawnManager.isEnabled()) {
                if (spawnManager.isPresent()) {
                    Spawn spawn = spawnManager.getSpawn();
                    TeleportMode mode = new TeleportMode(Mode.SPAWN, null, spawn, null);
                    final Location loc = spawn.getLocation();
                    if (args.length == 1 && sender.hasPermission("sst.spawn.others")) {
                        Player toTeleport = Bukkit.getPlayer(args[0]);
                        if (toTeleport != null) {
                            teleportManager.teleport(toTeleport, loc, mode, spawnManager.getSettings());
                        } else {
                            sender.sendMessage(Utils.getString("other-messages.spawn.player-not-found", sender));
                        }
                    } else if (args.length == 0 && sender instanceof Player) {
                        teleportManager.teleport((Player) sender, loc, mode, spawnManager.getSettings());
                    } else {
                        sender.sendMessage(Utils.getString("no-permission", sender));
                    }
                } else {
                    sender.sendMessage(Utils.getString("other-messages.spawn.spawn-not-set", sender));
                }
            } else {
                spawnManager.sendWarnIfEnabled(sender);
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

}
