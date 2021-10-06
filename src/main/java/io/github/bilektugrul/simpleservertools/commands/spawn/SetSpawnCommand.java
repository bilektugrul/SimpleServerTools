package io.github.bilektugrul.simpleservertools.commands.spawn;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetSpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SetSpawnCommand(SST plugin) {
        this.spawnManager = plugin.getSpawnManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p && sender.hasPermission("sst.setspawn")) {
            Location newLoc = p.getLocation();
            if (spawnManager.getSpawn() == null) {
                spawnManager.setSpawn(newLoc);
                p.sendMessage(Utils.getMessage("spawn.created", p)
                        .replace("%spawnlocation%", LocationSerializer.toString(newLoc)));
            } else {
                spawnManager.getSpawn().setLocation(newLoc);
                p.sendMessage(Utils.getMessage("spawn.changed", p)
                        .replace("%spawnlocation%", LocationSerializer.toString(newLoc)));
                spawnManager.saveSpawn();
            }
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}