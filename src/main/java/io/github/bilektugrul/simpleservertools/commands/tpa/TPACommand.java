package io.github.bilektugrul.simpleservertools.commands.tpa;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TPACommand implements CommandExecutor {

    private final TPAManager tpaManager;
    private final UserManager userManager;

    public TPACommand(SST plugin) {
        this.tpaManager = plugin.getTPAManager();
        this.userManager = plugin.getUserManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender.hasPermission("sst.tpa") && sender instanceof Player) {
            Player p = (Player) sender;
            User playerUser = userManager.getUser(p);
            if (playerUser.isAvailable() && playerUser.isAcceptingTPA()) {
                if (args.length == 1) {
                    Player toTeleport = Bukkit.getPlayer(args[0]);
                    if (toTeleport != null && !toTeleport.equals(sender) && !tpaManager.isPresent(toTeleport, p)) {
                        User toTeleportUser = userManager.getUser(toTeleport);
                        if (toTeleportUser.isAcceptingTPA() && toTeleportUser.isAvailable()) {
                            tpaManager.startWaitTask(p, toTeleport);
                            String pName = p.getName();
                            p.sendMessage(Utils.getString("other-messages.tpa.request-sent", p)
                                    .replace("%teleporting%", toTeleport.getName()));
                            toTeleport.sendMessage(Utils.getString("other-messages.tpa.new-request", toTeleport)
                                    .replace("%requester%", pName));
                            TextComponent component = new TextComponent(Utils.getString("other-messages.tpa.click-to-accept", toTeleport));
                            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + pName));
                            BaseComponent baseComponent = new TextComponent("\n" + Utils.getString("other-messages.tpa.click-to-deny", toTeleport));
                            baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + pName));
                            component.addExtra(baseComponent);
                            toTeleport.sendMessage(component);
                            p.sendMessage(Utils.getString("other-messages.tpa.extra-messages", p));
                            toTeleport.sendMessage(Utils.getString("other-messages.tpa.extra-messages", toTeleport));
                        } else {
                            p.sendMessage(Utils.getString("other-messages.tpa.not-now-2", p));
                        }
                    } else {
                        p.sendMessage(Utils.getString("other-messages.tpa.not-found", p));
                    }
                } else {
                    p.sendMessage(Utils.getString("other-messages.tpa.usage", p));
                }
            } else {
                p.sendMessage(Utils.getString("other-messages.tpa.not-now", p));
            }
        } else {
            sender.sendMessage(Utils.getString("no-permission", sender));
        }
        return true;
    }

}
