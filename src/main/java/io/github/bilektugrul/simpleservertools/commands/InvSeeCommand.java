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
        if (sender instanceof Player && sender.hasPermission("sst.invsee")) {
            Player senderPlayer = (Player) sender;
             if (args.length >= 1) {
                Player invPlayer = Bukkit.getPlayer(args[0]);
                if (invPlayer != null) {
                    if (args.length == 1) {
                        senderPlayer.openInventory(invPlayer.getInventory());
                    } else if (args[1].equalsIgnoreCase("zırh")) {
                        Inventory armorInventory = Bukkit.getServer().createInventory(senderPlayer, 9, Utils.getString("other-messages.invsee.title", senderPlayer)) ;
                        List<ItemStack> list = Arrays.asList(invPlayer.getInventory().getArmorContents());
                        Collections.reverse(list);
                        armorInventory.setContents(list.toArray(new ItemStack[0]));
                        if (armorInventory.getContents().length > 0) senderPlayer.openInventory(armorInventory);
                        else senderPlayer.sendMessage(Utils.getString("other-messages.invsee.armor-empty", senderPlayer));
                    } else {
                        senderPlayer.sendMessage(Utils.getString("other-messages.invsee.wrong-usage", senderPlayer));
                    }
                } else {
                    senderPlayer.sendMessage(Utils.getString("other-messages.invsee.not-found", senderPlayer));
                }
            }
        }
        return true;
    }

}
