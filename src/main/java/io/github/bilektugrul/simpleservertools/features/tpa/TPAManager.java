package io.github.bilektugrul.simpleservertools.features.tpa;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportManager;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TPAManager {

    private TeleportSettings settings;
    private final Map<Player, Set<Player>> tpaList = new HashMap<>();
    private final TeleportManager teleportManager;
    private final SST plugin;

    public TPAManager(SST plugin) {
        this.teleportManager = plugin.getTeleportManager();
        this.plugin = plugin;
        loadSettings();
    }

    public boolean isPresent(Player key) {
        return tpaList.containsKey(key);
    }

    public boolean isPresent(Player key, Player value) {
        if (isPresent(key)) return tpaList.get(key).contains(value);
        else return false;
    }

    public void add(Player p, Player to) {
        if (isPresent(to)) {
            tpaList.get(to).add(p);
        } else {
            Set<Player> set = new HashSet<>();
            set.add(p);
            tpaList.put(to, set);
        }
    }

    public void remove(Player p, Player from) {
        if (isPresent(from)) {
            tpaList.get(from).remove(p);
        }
    }

    public void startWaitTask(Player p, Player to) {
        add(p, to);
        new BukkitRunnable() {

            int i = 0;
            final int max = Utils.getInt("tpa.accept-time");

            @Override
            public void run() {
                i++;
                if (isPresent(to, p) && i == max) {
                    remove(p, to);
                    p.sendMessage(Utils.getMessage("tpa.request-cancelled-2", p)
                            .replace("%teleporting%", to.getName()));
                    to.sendMessage(Utils.getMessage("tpa.request-cancelled", to)
                            .replace("%requester%", p.getName()));
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 20);
    }

    public void teleport(Player p, Player to, Location loc, TeleportMode teleportMode) {
        remove(p, to);
        teleportManager.teleport(p, loc, teleportMode, getSettings());
    }

    public void loadSettings() {
        final int time = Utils.getInt("tpa.teleport-time");

        final boolean blockMove = Utils.getBoolean("tpa.cancel-when-move.settings.block-move");
        final boolean blockCommands = Utils.getBoolean("tpa.block-commands.enabled");
        final boolean cancelTeleportOnMove = Utils.getBoolean("tpa.cancel-when-move.settings.cancel-teleport");
        final boolean blockDamage = Utils.getBoolean("tpa.cancel-damage.settings.block-damage");
        final boolean cancelTeleportOnDamage = Utils.getBoolean("tpa.cancel-damage.settings.cancel-teleport");
        final boolean staffBypassTime = Utils.getBoolean("tpa.staff-bypass-time");

        final CancelMode cancelMoveMode = CancelMode.valueOf(Utils.getString("tpa.cancel-when-move.mode", null));
        final CancelMode cancelDamageMode = CancelMode.valueOf(Utils.getString("tpa.cancel-damage.mode", null));
        final CancelMode cancelCommandsMode = CancelMode.valueOf(Utils.getString("tpa.cancel-damage.mode", null));

        settings = new TeleportSettings(time, blockMove, cancelTeleportOnMove, cancelMoveMode, blockDamage, cancelTeleportOnDamage, cancelDamageMode, staffBypassTime, blockCommands, cancelCommandsMode);
    }
    
    public TeleportSettings getSettings() {
        if (settings == null) {
            loadSettings();
        }
        return settings;
    }

}
