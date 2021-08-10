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
        if (!sender.hasPermission("sst.skull")) {
            Utils.noPermission(sender);
            return true;
        }

        boolean isPlayer = sender instanceof Player;

        Player toGive = args.length >= 2
                ? Bukkit.getPlayer(args[1])
                : isPlayer ? (Player) sender : null;
        String skullOwner = args.length >= 1 ? args[0] : isPlayer ? sender.getName() : null;

        if (skullOwner == null) {
            sender.sendMessage(Utils.getMessage("skull.wrong-usage", sender));
            return true;
        }

        if (toGive == null) {
            sender.sendMessage(Utils.getMessage("skull.not-found", sender));
            return true;
        }

        if (!NAME_PATTERN.matcher(skullOwner).matches()) {
            sender.sendMessage(Utils.getMessage("skull.wrong-usage", sender));
            return true;
        }

        if (!toGive.getName().equals(Utils.matchName(sender)) && !sender.hasPermission("sst.skull.others")) {
            Utils.noPermission(sender);
            return true;
        }

        giveSkull(sender, toGive, skullOwner);
        return true;
    }

    private void giveSkull(CommandSender executor, Player toGive, String owner) {
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack skull = new ItemStack(SKULL_ITEM);
                SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
                skullMeta.setDisplayName("Skull of " + owner);
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
