package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class TPAllCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("sst.tpall")) {
            Player p = (Player) sender;
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.teleport(p);
            }
            p.sendMessage(Utils.getString("other-messages.tpall.teleported", p)
                    .replace("%size%", String.valueOf(players.size())));
        }
        return true;
    }

}
