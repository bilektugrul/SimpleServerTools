package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AFKCommand implements CommandExecutor {

    private final UserManager userManager;

    public AFKCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player && sender.hasPermission("sst.afk")) {
            User user = userManager.getUser(player);
            user.setAfk(!user.isAfk());
            player.sendMessage(Utils.getMessage("afk.toggled", player)
                    .replace("%mode%", Utils.getMessage("afk.modes." + user.isAfk())));
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}
