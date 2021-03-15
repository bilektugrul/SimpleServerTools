package io.github.bilektugrul.simpleservertools.commands.tpa;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
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

    public TPAToggleCommand(SimpleServerTools plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.tpatoggle") && sender instanceof Player) {
            Player p = (Player) sender;
            User user = userManager.getUser(p);
            boolean newMode = !user.isAcceptingTPA();
            user.setAcceptingTPA(newMode);
            p.sendMessage(Utils.getString("other-messages.tpa.toggled", p)
                    .replace("%newmode%", Utils.getString("other-messages.tpa.modes." + newMode, p)));
         }
        return true;
    }

}
