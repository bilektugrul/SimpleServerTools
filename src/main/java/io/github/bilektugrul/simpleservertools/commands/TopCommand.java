package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TopCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("sst.top")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        Player p = (Player) sender;
        Location loc = p.getLocation();
        World world = p.getWorld();
        p.teleport(world.getHighestBlockAt(loc).getLocation());

        return true;
    }

}
