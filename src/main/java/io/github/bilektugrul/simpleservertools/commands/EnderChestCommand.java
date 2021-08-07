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
            Player holder = p;

            if (args.length >= 1) {
                if (sender.hasPermission("sst.enderchest.others")) {
                    holder = Bukkit.getPlayer(args[0]);
                } else {
                    Utils.noPermission(sender);
                    return true;
                }
                
            }

            if (holder != null) {
                p.closeInventory();
                p.openInventory(holder.getEnderChest());
            } else {
                p.sendMessage(Utils.getMessage("ender-chest.not-found", p));
            }
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}
