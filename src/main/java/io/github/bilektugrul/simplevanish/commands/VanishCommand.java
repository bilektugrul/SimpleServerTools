package io.github.bilektugrul.simplevanish.commands;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import org.bukkit.command.CommandExecutor;
import org.jetbrains.annotations.NotNull;

public class VanishCommand implements CommandExecutor {

    public List<UUID> vanishPlayers = new ArrayList<>();
    public List<UUID> onlineVanishPlayers = new ArrayList<>();

    String permission = SimpleVanish.plugin.getString("vanish-command-permission");
    public static VanishCommand instance;

    public VanishCommand(SimpleVanish main) {
        main.getServer().getPluginCommand("simplevanish").setExecutor(this);
        if (instance == null) {
            instance = this;
        } else {
            throw new IllegalStateException("SimpleVanish cannot be initialized more than 1 time.");
        }
    }

    public boolean isVanished(UUID uuid) {
        return vanishPlayers.contains(uuid);
    }

    public boolean onCommand(CommandSender sender, @NotNull Command cmd, @NotNull String s, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (player.hasPermission(permission)) {
                if (args.length == 0) {
                    if (!isVanished(player.getUniqueId())) hidePlayer(player, false);
                    else showPlayer(player, false);
                } else if (args[0].equalsIgnoreCase("reload")) {
                    SimpleVanish.plugin.reloadConfig();
                    player.sendMessage(SimpleVanish.plugin.getString("config-reloaded", player));
                } else {
                    player.sendMessage("§c/vanish [<reload>]");
                }
            }
            else {
                player.sendMessage(SimpleVanish.plugin.getString("no-permission", player));
            }
        }
        return true;
    }

    public void hidePlayer(Player player, boolean silent) {
        vanishPlayers.remove(player.getUniqueId());
        onlineVanishPlayers.remove(player.getUniqueId());
        for (Player p : Bukkit.getOnlinePlayers()) { if (!p.hasPermission(permission)) { p.hidePlayer(player); } }
        vanishPlayers.add(player.getUniqueId());
        onlineVanishPlayers.add(player.getUniqueId());
        player.sendMessage(SimpleVanish.plugin.getString("vanish-activated", player));
        if (SimpleVanish.plugin.getBoolean("join-quit-messages.enabled", false))
            if (!silent)
                Bukkit.broadcastMessage(SimpleVanish.plugin.getString("join-quit-messages.quit-message", player));
    }

    public void showPlayer(Player player, boolean silent) {
        for (Player p : Bukkit.getOnlinePlayers()) { p.showPlayer(player); }
        player.sendMessage(SimpleVanish.plugin.getString("vanish-disabled", player));
        vanishPlayers.remove(player.getUniqueId());
        onlineVanishPlayers.remove(player.getUniqueId());
        if (SimpleVanish.plugin.getBoolean("join-quit-messages.enabled", false))
            if (!silent)
                Bukkit.broadcastMessage(SimpleVanish.plugin.getString("join-quit-messages.join-message", player));
    }
}
