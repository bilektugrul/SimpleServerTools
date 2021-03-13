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
        if (sender.hasPermission("sst.feed")) {
            Player feedPlayer = null;
            if (args.length >= 1) {
                feedPlayer = Bukkit.getPlayer(args[0]);
            } else if (sender instanceof Player) {
                feedPlayer = (Player) sender;
            }
            if (feedPlayer != null) {
                feed(feedPlayer);
                if (feedPlayer.equals(sender)) {
                    feedPlayer.sendMessage(Utils.getString("other-messages.feed.message", feedPlayer));
                } else {
                    sender.sendMessage(Utils.getString("other-messages.feed.message-other", sender)
                            .replace("%other%", feedPlayer.getName()));
                }
            } else {
                sender.sendMessage(Utils.getString("other-messages.feed.not-found", sender));
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

    private void feed(Player player)  {
        int max = 30;
        FoodLevelChangeEvent event = new FoodLevelChangeEvent(player, max);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            player.setFoodLevel(Math.min(event.getFoodLevel(), 20));
            player.setSaturation(10);
            player.setExhaustion(0F);
        }
    }

}