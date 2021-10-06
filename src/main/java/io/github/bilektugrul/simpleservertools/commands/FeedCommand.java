package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.jetbrains.annotations.NotNull;

public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.feed")) {
            Utils.noPermission(sender);
            return true;
        }

        Player feedPlayer = args.length > 0 ? Bukkit.getPlayer(args[0]) : sender instanceof Player ? (Player) sender : null;

        if (feedPlayer == null) {
            sender.sendMessage(Utils.getMessage("feed.not-found", sender));
            return true;
        }

        feed(feedPlayer, sender);
        return true;
    }

    private void feed(Player player, CommandSender from)  {

        boolean isSame = player.equals(from);
        if (!isSame && !from.hasPermission("sst.feed.others")) {
            from.sendMessage(Utils.getMessage("no-permission", from));
            return;
        }

        int max = 30;
        FoodLevelChangeEvent event = new FoodLevelChangeEvent(player, max);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.setFoodLevel(Math.min(event.getFoodLevel(), 20));
            player.setSaturation(10);
            player.setExhaustion(0F);
            if (isSame) {
                player.sendMessage(Utils.getMessage("feed.message", player));
            } else {
                from.sendMessage(Utils.getMessage("feed.message-other", from)
                        .replace("%other%", player.getName()));
            }
        }
    }

}