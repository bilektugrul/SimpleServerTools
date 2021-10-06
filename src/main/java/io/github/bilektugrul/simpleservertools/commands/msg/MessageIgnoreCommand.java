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

public class MessageIgnoreCommand implements CommandExecutor {

    private final UserManager userManager;

    public MessageIgnoreCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.msgignore") && sender instanceof Player p) {

            if (args.length == 0) {
                sender.sendMessage(Utils.getMessage("msg.wrong-ignore"));
                return true;
            }

            String block = args[0];
            User user = userManager.getUser(p);
            boolean toggledTo = user.toggleMsgBlock(block);

            p.sendMessage(Utils.getMessage("msg.block-toggled", p)
                    .replace("%blocked%", block)
                    .replace("%toggledto%", Utils.getMessage("msg.block-modes." + toggledTo, p)));
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}