package io.github.bilektugrul.simpleservertools.commands.msg;

import io.github.bilektugrul.simpleservertools.SST;
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

    private final HashMap<String, String> replyList = new HashMap<>();

    private final UserManager userManager;
    private final VanishManager vanishManager;

    public MessageCommand(SST plugin) {
        userManager = plugin.getUserManager();
        vanishManager = plugin.getVanishManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.msg")) {
            if (args.length >= 1) {
                String format = Utils.getString("other-messages.msg.format", sender)
                        .replace("%from%", Utils.matchName(sender));

                String to;
                boolean reply = false;

                if (label.startsWith("r") || label.equalsIgnoreCase("yanıt")) {
                    reply = true;
                    to = replyList.get(sender.getName());
                } else {
                    to = args[0];
                }

                if (to != null) {
                    Player toPlayer = Bukkit.getPlayer(to);
                    if (toPlayer != null) {
                        String toPlayerName = toPlayer.getName();
                        User senderUser = null;

                        if (sender instanceof Player) {
                            senderUser = userManager.getUser((Player) sender);
                        }

                        User toUser = userManager.getUser(toPlayer);
                        UUID toUserUUID = toUser.getUUID();
                        boolean vanishMsgState = Utils.getBoolean("vanish.messages-to-vanished-players", false);

                        if (!vanishMsgState && vanishManager.isVanished(toUserUUID)) {
                            sender.sendMessage(Utils.getString("other-messages.msg.not-found", sender));
                            return true;
                        }

                        if (toUser.isAcceptingMsg()) {
                            if (senderUser != null && senderUser.isAcceptingMsg()) {
                                if (!reply && args.length == 1) {
                                    sender.sendMessage(Utils.getString("other-messages.msg.wrong-usage", sender));
                                } else {
                                    format = format.replace("%to%", toPlayerName)
                                            .replace("%msg%", Utils.arrayToString(Arrays.copyOfRange(args, reply ? 0 : 1, args.length), sender, false, false));
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
                    } else {
                        sender.sendMessage(Utils.getString("other-messages.msg.not-found", sender));
                    }
                }

            } else {
                sender.sendMessage(Utils.getString("other-messages.msg.wrong-usage", sender));
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }
}
