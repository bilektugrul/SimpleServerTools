package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class InvSeeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player senderPlayer) || !sender.hasPermission("sst.invsee")) {
            Utils.noPermission(sender);
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("invsee.wrong-usage"));
            return true;
        }

        Player invPlayer = Bukkit.getPlayer(args[0]);
        if (invPlayer == null) {
            senderPlayer.sendMessage(Utils.getMessage("invsee.not-found", senderPlayer));
            return true;
        }

        if (args.length == 1) {
            senderPlayer.openInventory(invPlayer.getInventory());
            return true;
        }

        if (args[1].contains("zırh") || args[1].contains("armor")) {
            List<ItemStack> list = Arrays.asList(invPlayer.getInventory().getArmorContents());
            if (list.isEmpty()) {
                senderPlayer.sendMessage(Utils.getMessage("invsee.armor-empty", senderPlayer));
                return true;
            }

            Inventory armorInventory = Bukkit.getServer().createInventory(senderPlayer, 9, Utils.getMessage("invsee.title", senderPlayer));
            Collections.reverse(list);
            armorInventory.setContents(list.toArray(new ItemStack[0]));
            senderPlayer.openInventory(armorInventory);
        } else {
            senderPlayer.sendMessage(Utils.getMessage("invsee.wrong-usage", senderPlayer));
        }
        return true;
    }
}