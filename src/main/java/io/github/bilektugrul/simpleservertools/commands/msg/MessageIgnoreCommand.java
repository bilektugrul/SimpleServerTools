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
        if (!sender.hasPermission("sst.msgignore") || !(sender instanceof Player senderPlayer)) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("msg.wrong-ignore"));
            return true;
        }

        String toBlock = args[0];
        User user = userManager.getUser(senderPlayer);
        boolean toggledTo = user.toggleMsgBlock(toBlock);

        senderPlayer.sendMessage(Utils.getMessage("msg.block-toggled", senderPlayer)
                .replace("%blocked%", toBlock)
                .replace("%toggledto%", Utils.getMessage("msg.block-modes." + toggledTo, senderPlayer)));
        return true;
    }

}