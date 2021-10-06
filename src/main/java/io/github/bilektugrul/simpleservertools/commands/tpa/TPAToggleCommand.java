package io.github.bilektugrul.simpleservertools.commands.tpa;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPAToggleCommand implements CommandExecutor {

    private final UserManager userManager;

    public TPAToggleCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.tpatoggle") && sender instanceof Player p) {
            User user = userManager.getUser(p);
            boolean newMode = !user.isAcceptingTPA();
            user.setAcceptingTPA(newMode);
            p.sendMessage(Utils.getMessage("tpa.toggled", p)
                    .replace("%newmode%", Utils.getMessage("tpa.modes." + newMode, p)));
         } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}