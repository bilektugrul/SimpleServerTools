package io.github.bilektugrul.simpleservertools.commands.speed;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("sst.speed")) {
            Player player = (Player) sender;
            SpeedMode mode = new SpeedMode(player);
            if (args.length == 1) {
                mode.setSpeed(Float.parseFloat(args[0]));
            }
        }
        return true;
    }

}
