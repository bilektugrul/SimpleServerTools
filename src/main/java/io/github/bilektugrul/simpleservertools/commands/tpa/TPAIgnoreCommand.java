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

public class TPAIgnoreCommand implements CommandExecutor {

    private final UserManager userManager;

    public TPAIgnoreCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.tpaignore") || !(sender instanceof Player senderPlayer)) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("tpa.wrong-ignore"));
            return true;
        }

        String block = args[0];
        User user = userManager.getUser(senderPlayer);
        boolean toggledTo = user.toggleTPABlock(block);

        senderPlayer.sendMessage(Utils.getMessage("tpa.block-toggled", senderPlayer)
                .replace("%blocked%", block)
                .replace("%toggledto%", Utils.getMessage("tpa.block-modes." + toggledTo, senderPlayer)));
        return true;
    }

}