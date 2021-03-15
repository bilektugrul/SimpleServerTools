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

    private final SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);
    private final TeleportManager teleportManager = plugin.getTeleportManager();

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
    private final String mode;

    public TeleportTask(Player player, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        p = player;
        settings = teleportSettings;
        mode = teleportMode.getModeString();
        isStaff = player.hasPermission("sst.staff");

        time = isStaff && settings.getStaffBypassTime()
                ? 0
                : settings.getTime();

        teleportingMsg = Utils.getString("other-messages." + mode + ".teleporting.message", player);
        teleportingMode = Utils.getString("other-messages." + mode + ".teleporting.mode", player);
        teleportingSub = "";

        teleportedMsg = Utils.getString("other-messages." + mode + ".teleported.message", player);
        teleportedMode = Utils.getString("other-messages." + mode +".teleported.mode", player);
        teleportedSub = "";

        firstLoc = player.getLocation();
        finalLoc = loc;
        firstHealth = player.getHealth();

        UUID uuid = player.getUniqueId();

        cancelMoveMode = settings.getCancelMoveMode();
        cancelDamageMode = settings.getCancelDamageMode();
        UserManager userManager = plugin.getUserManager();
        user = userManager.getUser(player);

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
            String teleportingTo = teleportMode.getTPAInfo().getToTeleport().getName();
            teleportedMsg = teleportedMsg.replace("%teleporting%", teleportingTo);
            teleportingMsg = teleportingMsg.replace("%teleporting%", teleportingTo);
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
                    cancelTeleport(true);
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
                if (settings.getCancelTeleportOnDamage()) cancelTeleport(true);
            }
        }

        if (time == 0) {
            user.setState(User.State.PLAYING);
            PaperLib.teleportAsync(p, finalLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Utils.sendMessage(p, teleportedMode, teleportedMsg, teleportedSub, String.valueOf(time));
            teleportManager.getTeleportTasks().remove(this);
            cancelTeleport(false);
            return;
        }

        Utils.sendMessage(p, teleportingMode, teleportingMsg, teleportingSub, String.valueOf(time));
        time--;
    }

    public void cancelTeleport(boolean msg) {
        user.setState(User.State.PLAYING);
        if (msg) p.sendMessage(Utils.getString("other-messages." + mode + ".teleport-cancelled", p));
        teleportManager.getTeleportTasks().remove(this);
        super.cancel();
    }

}
