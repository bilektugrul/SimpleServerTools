package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.jetbrains.annotations.NotNull;

public class HealCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.heal")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        Player healPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        if (healPlayer == null) {
            sender.sendMessage(Utils.getMessage("heal.not-found", sender));
            return true;
        }

        heal(healPlayer, sender);
        return true;
    }

    private void heal(Player player, CommandSender from)  {

        boolean isSame = player.equals(from);
        if (!isSame && !from.hasPermission("sst.heal.others")) {
            from.sendMessage(Utils.getMessage("no-permission", from));
            return;
        }

        double max = player.getMaxHealth();
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, max, EntityRegainHealthEvent.RegainReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.setFoodLevel(20);
            player.setHealth(max);
            player.setFireTicks(0);
            if (isSame) {
                player.sendMessage(Utils.getMessage("heal.message", player));
            } else {
                from.sendMessage(Utils.getMessage("heal.message-other", from)
                        .replace("%other%", player.getName()));
            }
        }
    }

}
