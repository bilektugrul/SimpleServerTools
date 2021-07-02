package io.github.bilektugrul.simpleservertools.commands.msg;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.spy.SpyManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class MessageCommand implements CommandExecutor {

    private final HashMap<String, String> replyMap = new HashMap<>();

    private final UserManager userManager;
    private final VanishManager vanishManager;
    private final SpyManager spyManager;

    public MessageCommand(SST plugin) {
        userManager = plugin.getUserManager();
        vanishManager = plugin.getVanishManager();
        spyManager = plugin.getSpyManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("sst.msg")) {
            sender.sendMessage(Utils.getMessage("no-permission", sender));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(Utils.getMessage("msg.wrong-usage", sender));
            return true;
        }

        String senderName = Utils.matchName(sender);
        String format = Utils.getMessage("msg.format", sender)
                .replace("%from%", senderName);

        String to = args[0];
        boolean reply = false;

        if (label.startsWith("r") || label.equalsIgnoreCase("yanıt")) {
            reply = true;
            to = replyMap.get(senderName);
            if (to == null) {
                sender.sendMessage(Utils.getMessage("msg.no-reply-player", sender));
                return true;
            }
        }

        Player toPlayer = Bukkit.getPlayer(to);
        if (toPlayer == null) {
            sender.sendMessage(Utils.getMessage("msg.not-found", sender));
            return true;
        }

        String toPlayerName = toPlayer.getName();
        boolean senderAcceptMessages = true;

        if (sender instanceof Player) {
            User senderUser = userManager.getUser((Player) sender);
            senderAcceptMessages = senderUser.isAcceptingMsg();
        }

        User toUser = userManager.getUser(toPlayer);
        UUID toUserUUID = toUser.getUUID();
        boolean vanishMsgState = Utils.getBoolean("vanish.messages-to-vanished-players", false);

        if (!vanishMsgState && vanishManager.isVanished(toUserUUID)) {
            sender.sendMessage(Utils.getMessage("msg.not-found", sender));
            return true;
        }

        if (!toUser.isAcceptingMsg()) {
            sender.sendMessage(Utils.getMessage("msg.closed", sender)
                    .replace("%to%", toPlayerName));
            return true;
        }

        if (toUser.isBlockedMsgsFrom(senderName)) {
            sender.sendMessage(Utils.getMessage("msg.blocked", sender));
            return true;
        }

        if (!senderAcceptMessages) {
            sender.sendMessage(Utils.getMessage("msg.closed-self", sender));
            return true;
        }

        if (!reply && args.length == 1) {
            sender.sendMessage(Utils.getMessage("msg.wrong-usage", sender));
            return true;
        }

        format = format.replace("%to%", toPlayerName)
                .replace("%msg%", String.join(" ", Arrays.copyOfRange(args, reply ? 0 : 1, args.length)));
        toPlayer.sendMessage(format);
        sender.sendMessage(format);
        spyManager.sendMessageToSpies(format, sender, toPlayer);
        replyMap.put(senderName, toPlayerName);
        return true;
    }

}
