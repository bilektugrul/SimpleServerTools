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
        if (sender instanceof Player) {

            if (!sender.hasPermission("sst.tpa")) {
                sender.sendMessage(Utils.getMessage("messages.no-permission", sender));
                return true;
            }

            Player p = (Player) sender;
            User playerUser = userManager.getUser(p);

            if (!playerUser.isAvailable() || !playerUser.isAcceptingTPA()) {
                p.sendMessage(Utils.getMessage("messages.tpa.not-now", p));
                return true;
            }

            if (args.length == 0) {
                p.sendMessage(Utils.getMessage("messages.tpa.usage", p));
                return true;
            }

            Player toTeleport = Bukkit.getPlayer(args[0]);

            if (tpaManager.isPresent(toTeleport, p)) {
                p.sendMessage(Utils.getMessage("messages.tpa.not-now", p));
                return true;
            }

            if (toTeleport != null && !toTeleport.equals(sender)) {
                User toTeleportUser = userManager.getUser(toTeleport);
                if (toTeleportUser.isAcceptingTPA() && toTeleportUser.isAvailable()) {
                    tpaManager.startWaitTask(p, toTeleport);
                    String pName = p.getName();
                    p.sendMessage(Utils.getMessage("messages.tpa.request-sent", p)
                            .replace("%teleporting%", toTeleport.getName()));
                    toTeleport.sendMessage(Utils.getMessage("messages.tpa.new-request", toTeleport)
                            .replace("%requester%", pName));
                    TextComponent component = new TextComponent(Utils.getMessage("messages.tpa.click-to-accept", toTeleport));
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + pName));
                    BaseComponent baseComponent = new TextComponent("\n" + Utils.getMessage("messages.tpa.click-to-deny", toTeleport));
                    baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + pName));
                    component.addExtra(baseComponent);
                    toTeleport.sendMessage(component);
                    p.sendMessage(Utils.getMessage("messages.tpa.extra-messages", p));
                    toTeleport.sendMessage(Utils.getMessage("messages.tpa.extra-messages", toTeleport));
                } else {
                    p.sendMessage(Utils.getMessage("messages.tpa.not-now-2", p));
                }
            } else {
                p.sendMessage(Utils.getMessage("messages.tpa.not-found", p));
            }
        }
        return true;
    }

}
