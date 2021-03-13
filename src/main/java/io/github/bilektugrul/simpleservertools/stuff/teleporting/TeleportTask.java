package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class TeleportTask extends BukkitRunnable {

    private static final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);
    private static final UserManager userManager = plugin.getUserManager();
    private static final TeleportManager teleportManager = plugin.getTeleportManager();

    private final Player p;

    private final TeleportSettings settings;
    boolean isStaff;
    private int time;
    private String teleportingMsg;
    private final String teleportingMode;
    private String teleportingSub;
    private String teleportedMsg;
    private final String teleportedMode;
    private String teleportedSub;
    private final Location firstLoc;
    private final Location finalLoc;
    private final double firstHealth;

    private final CancelMode cancelMoveMode;
    private final CancelMode cancelDamageMode;
    private final User user;

    public TeleportTask(Player player, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        p = player;
        settings = teleportSettings;
        String mode1 = teleportMode.getModeString();
        isStaff = player.hasPermission("sst.staff");

        time = isStaff && settings.getStaffBypassTime()
                ? 0
                : settings.getTime();

        teleportingMsg = Utils.getString("other-messages." + mode1 + ".teleporting.message", player);
        teleportingMode = Utils.getString("other-messages." + mode1 + ".teleporting.mode", player);
        teleportingSub = "";

        teleportedMsg = Utils.getString("other-messages." + mode1 + ".teleported.message", player);
        teleportedMode = Utils.getString("other-messages." + mode1 +".teleported.mode", player);
        teleportedSub = "";

        firstLoc = player.getLocation();
        finalLoc = loc;
        firstHealth = player.getHealth();

        UUID uuid = player.getUniqueId();

        cancelMoveMode = settings.getCancelMoveMode();
        cancelDamageMode = settings.getCancelDamageMode();
        user = userManager.getUser(uuid);

        final TeleportMode.Mode mode = teleportMode.getMode();

        if (mode == TeleportMode.Mode.WARPS) {
            user.setState(User.State.TELEPORTING);
            String name = teleportMode.getWarp().getName();
            teleportedMsg = teleportedMsg.replace("%warp%", name);
            teleportingMsg = teleportingMsg.replace("%warp%", name);
        } else if (mode == TeleportMode.Mode.SPAWN) {
            user.setState(User.State.TELEPORTING_SPAWN);
        } else {
            user.setState(User.State.TELEPORTING_PLAYER);
            Player teleportingTo = teleportMode.getTPAInfo().getToTeleport();
            teleportedMsg = teleportedMsg.replace("%teleporting%", teleportingTo.getName());
            teleportingMsg = teleportingMsg.replace("%teleporting%", teleportingTo.getName());
        }

        if ((teleportingMode.equalsIgnoreCase("TITLE") && teleportingMsg.contains("\n"))) {
            int index = teleportingMsg.indexOf("\n");
            teleportingSub = teleportingMsg.split("\n")[1];
            teleportingMsg = teleportingMsg.substring(0, index);
        }
        if (teleportedMode.equalsIgnoreCase("TITLE") && teleportedMsg.contains("\n")) {
            int index = teleportedMsg.indexOf("\n");
            teleportedSub  = teleportedMsg.split("\n")[1];
            teleportedMsg = teleportedMsg.substring(0, index);
        }

    }

    @Override
    public void run() {
        if (!Utils.isSameLoc(firstLoc, p.getLocation())) {
            boolean cancel = cancelMoveMode == CancelMode.EVERYONE
                    || (cancelMoveMode == CancelMode.STAFF && isStaff)
                    || (cancelMoveMode == CancelMode.EXCEPT_STAFF && !isStaff);
            if (cancel) {
                if (settings.getCancelTeleportOnMove()) {
                    cancelTeleport(user, p);
                    return;
                }
                if (settings.getBlockMove()) p.teleport(firstLoc);
            }
        }

        if (p.getHealth() != firstHealth) {
            boolean cancel = cancelDamageMode == CancelMode.EVERYONE
                    || (cancelDamageMode == CancelMode.STAFF && isStaff)
                    || (cancelDamageMode == CancelMode.EXCEPT_STAFF && !isStaff);
            if (cancel) {
                if (settings.getBlockDamage()) {
                    p.setHealth(firstHealth);
                    return;
                }
                if (settings.getCancelTeleportOnDamage()) cancelTeleport(user, p);
            }
        }

        if (time == 0) {
            user.setState(User.State.PLAYING);
            PaperLib.teleportAsync(p, finalLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Utils.sendMessage(p, teleportedMode, teleportedMsg, teleportedSub, String.valueOf(time));
            teleportManager.getTeleportTasks().remove(this);
            cancel();
            return;
        }

        Utils.sendMessage(p, teleportingMode, teleportingMsg, teleportingSub, String.valueOf(time));
        time--;
    }

    public void cancelTeleport(User user, Player p) {
        user.setState(User.State.PLAYING);
        p.sendMessage(Utils.getString("other-messages.warps.teleport-cancelled", p));
        teleportManager.getTeleportTasks().remove(this);
        cancel();
    }

}