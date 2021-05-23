package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.converters.Converter;
import io.github.bilektugrul.simpleservertools.utils.converters.EssentialsWarpConverter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ConvertCommand implements CommandExecutor {

    private final SST plugin;

    public ConvertCommand(SST plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.convert")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.replacePlaceholders("%prefix% &cWrong usage.", sender, false, false)); // TODO: wtf is this
            return true;
        }

        if (args[0].equalsIgnoreCase("esswarps")) { // TODO: I know this is not a good way to make a convert command. There is just one converter rn, so doesn't matter for me.
            Converter converter = new EssentialsWarpConverter(plugin);
            converter.convert();
        }

        return true;
    }

}
