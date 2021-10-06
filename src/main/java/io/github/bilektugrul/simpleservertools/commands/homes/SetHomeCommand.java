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

public class SetHomeCommand implements CommandExecutor {

    private final UserManager userManager;

    public SetHomeCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !sender.hasPermission("sst.sethome")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.getMessage("homes.wrong-usage", player));
            return true;
        }

        String homeName = args[0];
        User user = userManager.getUser(player);

        if (!(user.getHomeByName(homeName) == null)) {
            player.sendMessage(Utils.getMessage("homes.already-exists", player)
                    .replace("%home%", homeName));
            return true;
        }

        int max = Utils.getMaxHomeAmount(player);
        int created = user.getHomes().size();

        if (created >= max) {
            player.sendMessage(Utils.getMessage("homes.reached-max", player)
                    .replace("%maximum%", String.valueOf(max))
                    .replace("%created%", String.valueOf(created)));
            return true;
        }

        user.createHome(new Home(homeName, player.getLocation()));
        player.sendMessage(Utils.getMessage("homes.created", player)
                .replace("%home%", homeName));
        return true;
    }

}