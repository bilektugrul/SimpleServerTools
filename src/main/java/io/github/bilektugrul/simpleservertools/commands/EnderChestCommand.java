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
        if (sender instanceof Player p && sender.hasPermission("sst.enderchest")) {
            Player holder = args.length >= 1
                    ? sender.hasPermission("sst.enderchest.others")
                            ? Bukkit.getPlayer(args[0])
                            : null
                    : p;

            if (holder != null) {
                p.closeInventory();
                p.openInventory(holder.getEnderChest());
            } else {
                Utils.noPermission(p);
            }
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}