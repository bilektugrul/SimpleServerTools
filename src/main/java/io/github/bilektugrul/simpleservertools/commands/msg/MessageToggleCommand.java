package io.github.bilektugrul.simpleservertools.commands.msg;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MessageToggleCommand implements CommandExecutor {

    private final UserManager userManager;

    public MessageToggleCommand(SST plugin) {
        userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.msgtoggle") && sender instanceof Player) {
            Player p = (Player) sender;
            User user = userManager.getUser(p);
            boolean newMode = !user.isAcceptingMsg();
            user.setAcceptingMsg(newMode);
            p.sendMessage(Utils.getString("other-messages.msg.toggled", p)
                    .replace("%newmode%", Utils.getString("other-messages.msg.modes." + newMode, p)));
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }
}
