package io.github.bilektugrul.simpleservertools.features.spy;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SpyManager {

    private final List<Player> spies = new ArrayList<>();

    public void toggleSpy(Player p, CommandSender from) {
        boolean isNotSame = !p.equals(from);

        if (isNotSame && !from.hasPermission("sst.socialspy.others")) {
            from.sendMessage(Utils.getMessage("no-permission", from));
            return;
        }

        boolean newMode = !isSpy(p);

        if (isSpy(p)) spies.remove(p);
        else spies.add(p);

        p.sendMessage(Utils.getMessage("spy.toggled", p)
                .replace("%newmode%", Utils.getMessage("spy.modes." + newMode, p)));
        if (isNotSame) {
            from.sendMessage(Utils.getMessage("spy.toggled-other", p)
                    .replace("%newmode%", Utils.getMessage("spy.modes." + newMode, p))
                    .replace("%other%", p.getName()));
        }
    }

    public boolean isSpy(Player p) {
        return spies.contains(p);
    }

    public void sendMessageToSpies(String message, CommandSender sender, Player receiver) {
        String finalMessage = Utils.getMessage("spy.prefix") + " " + message;
        spies.forEach(spy -> {
            if (!spy.equals(receiver) || !spy.equals(sender)) {
                spy.sendMessage(finalMessage);
            }
        }); 
    }

    public String getReadableSpyList() {
        if (!spies.isEmpty()) {
            List<String> spyNames = spies.stream()
                    .map(Player::getName)
                    .collect(Collectors.toList());
            return String.join(", ", spyNames);
        } else {
            return Utils.getMessage("spy.no-spy", null);
        }
    }

}
