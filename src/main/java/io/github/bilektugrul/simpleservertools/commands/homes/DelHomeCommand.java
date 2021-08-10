package io.github.bilektugrul.simpleservertools.commands.homes;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.Home;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DelHomeCommand implements CommandExecutor {

    private final UserManager userManager;

    public DelHomeCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !sender.hasPermission("sst.delhome")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.getMessage("homes.wrong-usage", player));
            return true;
        }

        String homeName = args[0];
        User user = userManager.getUser(player);
        Home home = user.getHomeByName(homeName);

        if (home == null) {
            player.sendMessage(Utils.getMessage("homes.not-created", player)
                    .replace("%home%", homeName));
            return true;
        }

        user.deleteHome(homeName);
        player.sendMessage(Utils.getMessage("homes.deleted", player)
                .replace("%home%", homeName));
        return true;
    }

}
