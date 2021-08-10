package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.clearchat")) {
            Utils.noPermission(sender);
            return true;
        }

        int limit = Utils.getLanguageInt("messages.clear-chat.lines");
        String lines = StringUtils.repeat(" \n", limit);
        boolean effectsStaff = Utils.getLanguageBoolean("messages.clear-chat.for-staffs.enabled");

        for (Player player : Bukkit.getOnlinePlayers()) { // broadcast will effect logs so sending to players is better
            if (player.hasPermission("sst.staff") && !effectsStaff) {
                continue;
            }
            player.sendMessage(lines);
        }

        String cleared = Utils.getMessage("clear-chat.cleared", sender);
        if (!cleared.isEmpty()) Bukkit.broadcastMessage(cleared.replace("%executor%", Utils.matchName(sender)));

        return true;
    }

}
