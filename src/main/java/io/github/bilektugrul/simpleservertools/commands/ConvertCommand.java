package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.converting.Converter;
import io.github.bilektugrul.simpleservertools.converting.ConverterManager;
import io.github.bilektugrul.simpleservertools.converting.FinalState;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ConvertCommand implements CommandExecutor {

    private final SST plugin;

    public ConvertCommand(SST plugin) {
        this.plugin = plugin;
    }

    private final List<String> timer = new ArrayList<>();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.convert")) {
            Utils.noPermission(sender);
            return true;
        }

        if (!Utils.getBoolean("convert-enabled")) {
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Wrong usage. /convert <converter alias>");
            return true;
        }

        if (!plugin.isConverterManagerReady()) {
            sender.sendMessage(ChatColor.RED + "Converter Manager is not ready. Please enable it.");
            return true;
        }

        ConverterManager converterManager = plugin.getConverterManager();

        Converter converter = converterManager.findConverter(args[0]);
        if (converter != null) {
            String name = converter.getName();
            if (!timer.contains(name)) {
                sender.sendMessage(ChatColor.GREEN + "Found converter: " + name + " made by " + converter.getAuthor());
                sender.sendMessage("Use the command again in 7 seconds to start converting.");
                timer.add(name);
                Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> timer.remove(name), 140);
            } else {
                timer.remove(name);
                FinalState state = converter.convert();
                switch (state) {
                    case COMPLETED -> {
                        sender.sendMessage(ChatColor.GREEN + "Convert successfully completed. Check console for more information.");
                        return true;
                    }
                    case ALMOST -> {
                        sender.sendMessage(ChatColor.RED + "Convert completed with some errors. Check console for more information.");
                        return true;
                    }
                    case UNSUCCESSFUL -> {
                        sender.sendMessage(ChatColor.RED + "Convert could not be completed. Check console for more information.");
                        return true;
                    }
                    case STILL_RUNNING -> {
                        sender.sendMessage(ChatColor.GREEN + "Converting process is still running in the background. A message will appear in the console when it's finished.");
                        return true;
                    }
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "There is no converter with name " + args[0]);
        }

        return true;
    }

}
