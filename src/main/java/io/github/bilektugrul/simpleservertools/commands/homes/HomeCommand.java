package io.github.bilektugrul.simpleservertools.commands.homes;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.Home;
import io.github.bilektugrul.simpleservertools.features.homes.HomeManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.Mode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HomeCommand implements CommandExecutor {

    private final UserManager userManager;
    private final TeleportManager teleportManager;
    private final HomeManager homeManager;

    public HomeCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
        this.teleportManager = plugin.getTeleportManager();
        this.homeManager = plugin.getHomeManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player) || !sender.hasPermission("sst.home")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(Utils.getMessage("homes.wrong-usage", player));
            return true;
        }

        String homeName = args[0];
        User user = userManager.getUser(player);

        if (homeName.equalsIgnoreCase("list")) {
            player.sendMessage(Utils.getMessage("homes.list", player)
                    .replace("%homeamount%", String.valueOf(user.getHomes().size()))
                    .replace("%homes%", user.readableHomeList()));
            return true;
        }

        Home home = user.getHomeByName(homeName);

        if (home == null) {
            player.sendMessage(Utils.getMessage("homes.not-created", player)
                    .replace("%home%", homeName));
            return true;
        }

        Location loc = home.location();
        TeleportMode mode = new TeleportMode(Mode.HOMES, home);
        teleportManager.teleport(player, loc, mode, homeManager.getSettings());
        return true;
    }

}