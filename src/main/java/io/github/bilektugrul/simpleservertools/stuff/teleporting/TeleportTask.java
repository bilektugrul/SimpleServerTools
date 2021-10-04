package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.MessageType;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserState;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

//TODO: this class can be better. dunno how but it can.
public class TeleportTask extends BukkitRunnable {

    private final TeleportManager teleportManager;

    private final Player player;
    private final User user;

    private final boolean isStaff;
    private final double firstHealth;
    private final TeleportSettings settings;
    private final MessageType teleportingMode, teleportedMode;
    private final Location firstLoc, finalLoc;
    private int time;
    private String teleportingMsg, teleportingSub, teleportedMsg, teleportedSub;

    private final CancelMode cancelMoveMode, cancelDamageMode;
    private final String mode;

    public TeleportTask(SST plugin, Player player, Location loc, TeleportMode teleportMode, TeleportSettings settings) {
        this.teleportManager = plugin.getTeleportManager();
        this.player = player;
        this.settings = settings;
        this.mode = teleportMode.getModeString();
        this.isStaff = player.hasPermission("sst.staff");

        this.time = isStaff && settings.doesStaffBypassTime()
                ? 0
                : settings.getTime();

        this.teleportingMsg = Utils.getMessage("" + mode + ".teleporting.message", player);
        this.teleportingMode = MessageType.valueOf(Utils.getMessage("" + mode + ".teleporting.mode", player));
        this.teleportingSub = "";

        this.teleportedMsg = Utils.getMessage("" + mode + ".teleported.message", player);
        this.teleportedMode = MessageType.valueOf(Utils.getMessage("" + mode +".teleported.mode", player));
        this.teleportedSub = "";

        this.firstLoc = player.getLocation();
        this.finalLoc = loc;
        this.firstHealth = player.getHealth();

        this.cancelMoveMode = settings.getCancelMoveMode();
        this.cancelDamageMode = settings.getCancelDamageMode();
        this.user = plugin.getUserManager().getUser(player);

        Mode mode = teleportMode.mode();

        switch (mode) {
            case WARPS -> {
                user.setState(UserState.TELEPORTING);
                String name = teleportMode.warp().name();
                teleportedMsg = teleportedMsg.replace("%warp%", name);
                teleportingMsg = teleportingMsg.replace("%warp%", name);
            }
            case SPAWN -> user.setState(UserState.TELEPORTING_SPAWN);
            case HOMES -> {
                user.setState(UserState.TELEPORTING_HOME);
                String name = teleportMode.home().name();
                teleportedMsg = teleportedMsg.replace("%home%", name);
                teleportingMsg = teleportingMsg.replace("%home%", name);
            }
            default -> {
                user.setState(UserState.TELEPORTING_PLAYER);
                String teleportingTo = teleportMode.tpaInfo().toTeleport().getName();
                teleportedMsg = teleportedMsg.replace("%teleporting%", teleportingTo);
                teleportingMsg = teleportingMsg.replace("%teleporting%", teleportingTo);
            }
        }
        setupTitle();
    }

    private void setupTitle() {
        if ((teleportingMode.name().contains("TITLE") || teleportingMode == MessageType.ALL) && teleportingMsg.contains("\n")) {
            int index = teleportingMsg.indexOf("\n");
            try {
                teleportingSub = teleportingMsg.split("\n")[1];
            } catch (ArrayIndexOutOfBoundsException ignored) {
                teleportingSub = "";
            }
            teleportingMsg = teleportingMsg.substring(0, index);
        }

        if ((teleportingMode.name().contains("TITLE") || teleportingMode == MessageType.ALL) && teleportedMsg.contains("\n")) {
            int index = teleportedMsg.indexOf("\n");
            try {
                teleportedSub = teleportedMsg.split("\n")[1];
            } catch (ArrayIndexOutOfBoundsException ignored) {
                teleportedSub = "";
            }
            teleportedMsg = teleportedMsg.substring(0, index);
        }
    }

    @Override
    public void run() {
        if (!Utils.isSameLoc(firstLoc, player.getLocation())) {
            boolean cancel = cancelMoveMode == CancelMode.EVERYONE
                    || (cancelMoveMode == CancelMode.STAFF && isStaff)
                    || (cancelMoveMode == CancelMode.EXCEPT_STAFF && !isStaff);
            if (cancel) {
                if (settings.doesCancelTeleportOnMove()) {
                    cancelTeleport(true);
                    return;
                }
                if (settings.doesBlockMove()) player.teleport(firstLoc);
            }
        }

        if (player.getHealth() != firstHealth) {
            boolean cancel = cancelDamageMode == CancelMode.EVERYONE
                    || (cancelDamageMode == CancelMode.STAFF && isStaff)
                    || (cancelDamageMode == CancelMode.EXCEPT_STAFF && !isStaff);
            if (cancel) {
                if (settings.doesBlockDamage()) {
                    player.setHealth(firstHealth);
                    return;
                }
                if (settings.doesCancelTeleportOnDamage()) cancelTeleport(true);
            }
        }

        if (time == 0) {
            user.setState(UserState.PLAYING);
            PaperLib.teleportAsync(player, finalLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Utils.sendMessage(player, teleportedMode, teleportedMsg, teleportedSub, String.valueOf(time));
            cancelTeleport(false);
            return;
        }

        Utils.sendMessage(player, teleportingMode, teleportingMsg, teleportingSub, String.valueOf(time));
        time--;
    }

    public void cancelTeleport(boolean msg) {
        user.setState(UserState.PLAYING);
        if (msg) player.sendMessage(Utils.getMessage(mode + ".teleport-cancelled", player));
        teleportManager.removeTeleportTask(this);
        super.cancel();
    }

}
