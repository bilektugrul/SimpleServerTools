package io.github.bilektugrul.simpleservertools.commands.spawn;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
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

    public SetSpawnCommand(SimpleServerTools plugin) {
        this.spawnManager = plugin.getSpawnManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("sst.setspawn")) {
            Player p = (Player) sender;
            Location newLoc = p.getLocation();
            if (spawnManager.getSpawn() == null) {
                spawnManager.setSpawn(newLoc);
                p.sendMessage(Utils.getString("other-messages.spawn.created", p)
                        .replace("%spawnlocation%", LocationSerializer.locationToString(newLoc)));
            } else {
                spawnManager.getSpawn().setLocation(newLoc);
                p.sendMessage(Utils.getString("other-messages.spawn.changed", p)
                        .replace("%spawnlocation%", LocationSerializer.locationToString(newLoc)));
                spawnManager.saveSpawn();
            }
        }
        return true;
    }

}
