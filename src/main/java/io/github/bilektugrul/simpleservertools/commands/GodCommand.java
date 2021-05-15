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
        if (sender.hasPermission("sst.god")) {
            Player godPlayer = null;
            boolean argGodMode = false;
            if (args.length >= 1) {
                godPlayer = Bukkit.getPlayer(args[0]);
                if (args.length >= 2) {
                    argGodMode = true;
                }
            }
            if (godPlayer == null && sender instanceof Player) godPlayer = (Player) sender;
            if (godPlayer != null) {
                User godUser = userManager.getUser(godPlayer);
                if (argGodMode) {
                    boolean newMode = Utils.matchMode(args[1]);
                    change(sender, godPlayer, godUser, newMode);
                } else {
                    change(sender, godPlayer, godUser, !godUser.isGod());
                }
            } else {
                sender.sendMessage(Utils.getMessage("god.type-player", sender));
            }
        }
        return true;
    }

    public void change(CommandSender from, Player godPlayer, User godUser, boolean newMode) {
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