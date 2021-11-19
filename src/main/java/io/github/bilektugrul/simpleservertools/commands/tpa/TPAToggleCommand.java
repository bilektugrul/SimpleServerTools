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
        if (!sender.hasPermission("sst.tpatoggle") || !(sender instanceof Player senderPlayer)) {
            Utils.noPermission(sender);
            return true;
        }

        User user = userManager.getUser(senderPlayer);
        boolean newMode = !user.isAcceptingTPA();
        user.setAcceptingTPA(newMode);

        senderPlayer.sendMessage(Utils.getMessage("tpa.toggled", senderPlayer)
                .replace("%newmode%", Utils.getMessage("tpa.modes." + newMode, senderPlayer)));
        return true;
    }

}