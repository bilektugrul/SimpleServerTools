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

// TODO: take a look at everything related to spawn system
public class SetSpawnCommand implements CommandExecutor {

    private final SpawnManager spawnManager;

    public SetSpawnCommand(SST plugin) {
        this.spawnManager = plugin.getSpawnManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!sender.hasPermission("sst.setspawn") || !(sender instanceof Player senderPlayer)) {
            Utils.noPermission(sender);
            return true;
        }

        Location newLoc = senderPlayer.getLocation();
        if (!spawnManager.isPresent()) {
            spawnManager.setSpawn(newLoc);
            senderPlayer.sendMessage(Utils.getMessage("spawn.created", senderPlayer)
                    .replace("%spawnlocation%", LocationSerializer.toString(newLoc)));
        } else {
            spawnManager.getSpawn().setLocation(newLoc);
            senderPlayer.sendMessage(Utils.getMessage("spawn.changed", senderPlayer)
                    .replace("%spawnlocation%", LocationSerializer.toString(newLoc)));
            spawnManager.saveSpawn();
        }
        return true;
    }

}