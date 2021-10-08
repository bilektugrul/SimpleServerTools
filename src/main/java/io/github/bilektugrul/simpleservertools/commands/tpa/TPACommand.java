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
        if (sender instanceof Player p) {

            if (!sender.hasPermission("sst.tpa")) {
                Utils.noPermission(sender);
                return true;
            }

            User playerUser = userManager.getUser(p);

            if (!playerUser.isAvailable() || !playerUser.isAcceptingTPA()) {
                p.sendMessage(Utils.getMessage("tpa.not-now", p));
                return true;
            }

            if (args.length == 0) {
                p.sendMessage(Utils.getMessage("tpa.usage", p));
                return true;
            }

            Player toTeleport = Bukkit.getPlayer(args[0]);
            if (toTeleport == null) {
                p.sendMessage(Utils.getMessage("tpa.player-not-found"));
                return true;
            }

            if (toTeleport.equals(sender)) {
                p.sendMessage(Utils.getMessage("tpa.not-yourself"));
                return true;
            }

            if (tpaManager.isPresent(toTeleport, p)) {
                p.sendMessage(Utils.getMessage("tpa.not-now", p));
                return true;
            }

            String pName = p.getName();
            User toTeleportUser = userManager.getUser(toTeleport);

            if (toTeleportUser.isBlockedTPAsFrom(pName)) {
                p.sendMessage(Utils.getMessage("tpa.blocked"));
                return true;
            }

            if (toTeleportUser.isAcceptingTPA() && toTeleportUser.isAvailable()) {
                if (tpaManager.startWaitTask(p, toTeleport)) {
                    p.sendMessage(Utils.getMessage("tpa.request-sent", p)
                            .replace("%teleporting%", toTeleport.getName()));
                    toTeleport.sendMessage(Utils.getMessage("tpa.new-request", toTeleport)
                            .replace("%requester%", pName));
                    if (Utils.getLanguageBoolean("messages.tpa.json.enabled")) {
                        TextComponent component = new TextComponent(Utils.getMessage("tpa.json.click-to-accept", toTeleport));
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept " + pName));
                        BaseComponent baseComponent = new TextComponent("\n" + Utils.getMessage("tpa.json.click-to-deny", toTeleport));
                        baseComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny " + pName));
                        component.addExtra(baseComponent);
                        toTeleport.sendMessage(component);
                    }
                    p.sendMessage(Utils.getMessage("tpa.extra-messages", p));
                    toTeleport.sendMessage(Utils.getMessage("tpa.extra-messages", toTeleport));
                }
            } else {
                p.sendMessage(Utils.getMessage("tpa.not-available", p));
            }
        } else {
            Utils.noPermission(sender);
        }
        return true;
    }

}