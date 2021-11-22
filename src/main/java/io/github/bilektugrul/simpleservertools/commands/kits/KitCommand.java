package io.github.bilektugrul.simpleservertools.commands.kits;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.kits.Kit;
import io.github.bilektugrul.simpleservertools.features.kits.KitManager;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class KitCommand implements CommandExecutor {

    private final UserManager userManager;
    private final KitManager kitManager;

    public KitCommand(SST plugin) {
        this.userManager = plugin.getUserManager();
        this.kitManager = plugin.getKitManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        boolean isAdmin = sender.hasPermission("sst.admin");
        boolean canSeeList = isAdmin || sender.hasPermission("sst.kitlist");
        boolean argsNotPresent = args.length == 0;
        boolean shouldSeeList = canSeeList && (argsNotPresent || args[0].equalsIgnoreCase("list"));

        if (shouldSeeList) {
            sendKitList(sender);
            return true;
        }

        Player toGive = args.length >= 2 ? Bukkit.getPlayer(args[1]) : sender instanceof Player ? (Player) sender : null;
        if (argsNotPresent || toGive == null) {
            sender.sendMessage(Utils.getMessage("kits.wrong-usage", sender));
            return true;
        }

        String arg = args[0];
        if (arg.equalsIgnoreCase("save") && isAdmin) {
            kitManager.saveKits();
            sender.sendMessage(Utils.getMessage("kits.saved", sender));
            return true;
        }

        Kit kit = kitManager.getKit(arg);
        User toGiveUser = userManager.getUser(toGive);
        kitManager.giveKit(sender, kit, toGive, toGiveUser);
        return true;
    }

    private void sendKitList(CommandSender sender) {
        sender.sendMessage(Utils.getMessage("kits.list", sender)
                .replace("%kitamount%", String.valueOf(kitManager.getKits().size()))
                .replace("%kits%", kitManager.readableKitList(sender)));
    }

}