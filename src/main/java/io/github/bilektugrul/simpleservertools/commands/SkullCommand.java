package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.item.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public class SkullCommand implements CommandExecutor {

    private final SST plugin;

    public SkullCommand(SST plugin) {
        this.plugin = plugin;
    }

    private final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]+$");
    private final ItemStack SKULL_ITEM = ItemUtils.PLAYER_HEAD_ITEM;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && sender.hasPermission("sst.skull")) {
            Player player = (Player) sender;
            Player toGive = player;
            final String skullOwner;
            if (args.length >= 1) {
                if (!NAME_PATTERN.matcher(args[0]).matches()) {
                    player.sendMessage(Utils.getMessage("skull.wrong-usage", player));
                    return true;
                }
                skullOwner = args[0];
                if (args.length >= 2) {
                    if (player.hasPermission("sst.skull.others")) {
                        toGive = Bukkit.getPlayer(args[1]);
                        if (toGive == null) {
                            player.sendMessage(Utils.getMessage("skull.not-found", player));
                            return true;
                        }
                    } else {
                        player.sendMessage(Utils.getMessage("no-permission", player));
                        return true;
                    }
                }
            } else {
                skullOwner = player.getName();
            }
            giveSkull(player, toGive, skullOwner);
        } else {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
        }
        return true;
    }

    private void giveSkull(Player executor, Player toGive, String owner) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack skull = new ItemStack(SKULL_ITEM);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setDisplayName(owner + " adlı oyuncunun kafası");
                skullMeta.setOwner(owner);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        skull.setItemMeta(skullMeta);
                        toGive.getInventory().addItem(skull);
                        toGive.sendMessage(Utils.getMessage("skull.added", toGive)
                                .replace("%headowner%", owner));
                        if (!toGive.equals(executor)) {
                            executor.sendMessage(Utils.getMessage("skull.added-other", executor)
                                    .replace("%headowner%", owner)
                                    .replace("%other%", toGive.getName()));
                        }
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

}
