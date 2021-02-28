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
        if (sender.hasPermission("sst.heal")) {
            Player healPlayer = null;
            if (args.length >= 1) {
                healPlayer = Bukkit.getPlayer(args[0]);
            } else if (sender instanceof Player) {
                healPlayer = (Player) sender;
            }
            if (healPlayer != null) {
                heal(healPlayer);
                if (healPlayer.equals(sender)) {
                    healPlayer.sendMessage(Utils.getString("other-messages.heal.message", healPlayer));
                } else {
                    sender.sendMessage(Utils.getString("other-messages.heal.message-other", sender)
                            .replace("%other%", healPlayer.getName()));
                }
            } else {
                sender.sendMessage(Utils.getString("other-messages.heal.not-found", sender));
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

    private void heal(Player player)  {
        double max = player.getMaxHealth();
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, max, EntityRegainHealthEvent.RegainReason.CUSTOM);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.setFoodLevel(20);
            player.setHealth(max);
            player.setFireTicks(0);
        }
    }

}
