package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class SSTCommand implements CommandExecutor {

    private final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission(SimpleServerTools.adminPerm)) {
                    plugin.reload();
                    sender.sendMessage(Utils.getPAPILessString("other-messages.config-reloaded")
                            .replace("%player%", sender instanceof Player ? sender.getName() : "CONSOLE"));
                }
            } else if (args[0].equalsIgnoreCase("help")) {
                if (args.length == 1) {
                    plugin.sendMessage(sender, "help-message");
                } else if (args[1].equalsIgnoreCase("warpArgs")) {
                    plugin.sendMessage(sender, "help-message.other.warpArgs.list");
                }
            }
        } else {
            plugin.sendMessage(sender, "help-message");
        }
        return true;
    }

}
