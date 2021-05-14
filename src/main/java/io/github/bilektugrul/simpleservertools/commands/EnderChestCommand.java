package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class EnderChestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("sst.enderchest")) {
            Player p = (Player) sender;
            Player holder = p;

            if (args.length >= 1) {
                holder = Bukkit.getPlayer(args[0]);
            }

            if (holder != null) {
                p.closeInventory();
                p.openInventory(holder.getEnderChest());
            } else {
                p.sendMessage(Utils.getMessage("messages.ender-chest.not-found", p));
            }
        } else {
            sender.sendMessage(Utils.getMessage("messages.no-permission", sender));
        }
        return true;
    }

}
