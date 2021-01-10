package io.github.bilektugrul.simplevanish.commands;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.listeners.PacketListener;
import io.github.bilektugrul.simplevanish.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    private SimpleVanish plugin = JavaPlugin.getPlugin(SimpleVanish.class);

    public VanishCommand(SimpleVanish main) {
        main.getServer().getPluginCommand("simplevanish").setExecutor(this);
    }

    private final String permission = Utils.getString("vanish-command-permission");

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (player.hasPermission(permission)) {
                    if (!Utils.isVanished(player.getUniqueId())) Utils.hidePlayer(player, false);
                    else Utils.showPlayer(player, false);
                } else {
                    player.sendMessage(Utils.getString("no-permission", player));
                }
            }
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                new PacketListener(plugin);
                sender.sendMessage(Utils.getString("config-reloaded")
                        .replace("%player%", sender instanceof Player ? sender.getName() : "CONSOLE"));
            } else {
                sender.sendMessage("§c/vanish [<reload>]");
            }
        }
        return true;
    }
}
