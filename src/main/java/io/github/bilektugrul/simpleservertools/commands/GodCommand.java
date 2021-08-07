package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GodCommand implements CommandExecutor {

    private final UserManager userManager;

    public GodCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.god")) {
            Utils.noPermission(sender);
            return true;
        }

        Player godPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        boolean argGodMode = args.length >= 2;

        if (godPlayer == null) {
            sender.sendMessage(Utils.getMessage("god.type-player", sender));
            return true;
        }

        User godUser = userManager.getUser(godPlayer);
        if (argGodMode) change(sender, godPlayer, godUser, Utils.matchMode(args[1]));
        else change(sender, godPlayer, godUser, !godUser.isGod());
        return true;
    }

    public void change(CommandSender from, Player godPlayer, User godUser, boolean newMode) {

        boolean isSame = from.equals(godPlayer);

        if (!isSame && !from.hasPermission("sst.god.others")) {
            from.sendMessage(Utils.getMessage("no-permission", from));
            return;
        }

        godUser.setGod(newMode);
        godPlayer.sendMessage(Utils.getMessage("god.toggled", godPlayer)
                .replace("%godmode%", Utils.getMessage("god.modes." + newMode, from)));
        if (!from.equals(godPlayer)) {
            from.sendMessage(Utils.getMessage("god.toggled-other", from)
                    .replace("%other%", godPlayer.getName())
                    .replace("%godmode%", Utils.getMessage("god.modes." + newMode, from)));
        }
    }

}