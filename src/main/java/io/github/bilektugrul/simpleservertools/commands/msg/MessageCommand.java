package io.github.bilektugrul.simpleservertools.commands.msg;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
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

public class MessageCommand implements CommandExecutor {

    private final HashMap<String, String> replyList = new HashMap<>();

    private final UserManager userManager;

    public MessageCommand(SimpleServerTools plugin) {
        userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.msg")) {
            if (args.length >= 1) {
                String format = Utils.getString("other-messages.msg.format", sender)
                        .replace("%from%", Utils.matchName(sender));
                String to = "";
                boolean reply = false;
                if (label.startsWith("r") || label.equalsIgnoreCase("yanıt")) {
                    reply = true;
                    to = replyList.get(sender.getName());
                } else {
                    to = args[0];
                } if (to != null) {
                    Player toPlayer = Bukkit.getPlayer(to);
                    if (toPlayer != null) {
                        String toPlayerName = toPlayer.getName();
                        User senderUser = null;
                        if (sender instanceof Player) senderUser = userManager.getUser((Player) sender);
                        User toUser = userManager.getUser(toPlayer);
                        if (toUser.isAcceptingMsg()) {
                            if (senderUser != null && senderUser.isAcceptingMsg()) {
                                if (!reply && args.length == 1) {
                                    sender.sendMessage(Utils.getString("other-messages.msg.wrong-usage", sender));
                                } else {
                                    format = format.replace("%to%", toPlayerName)
                                            .replace("%msg%", String.join(" ", Arrays.copyOfRange(args, reply ? 0 : 1, args.length)));
                                    toPlayer.sendMessage(format);
                                    sender.sendMessage(format);
                                    replyList.put(sender.getName(), toPlayerName);
                                }
                            } else {
                                sender.sendMessage(Utils.getString("other-messages.msg.closed-self", sender));
                            }
                        } else {
                            sender.sendMessage(Utils.getString("other-messages.msg.closed", sender)
                                .replace("%to%", toPlayerName));
                        }
                    }
                }
            } else {
                sender.sendMessage(Utils.getString("other-messages.msg.wrong-usage", sender));
            }
        }
        return true;
    }
}
