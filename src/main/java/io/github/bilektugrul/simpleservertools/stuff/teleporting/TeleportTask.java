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

public class TeleportTask extends BukkitRunnable {

    private final TeleportManager teleportManager;

    private final Player player;
    private final User user;

    private final boolean isStaff;
    private final double firstHealth;
    private final TeleportSettings settings;
    private final MessageType teleportingMode, teleportedMode;
    private final Location firstLoc, finalLoc;
    private final TeleportMessage teleportingMessages, teleportedMessages;
    private int time;

    private final CancelMode cancelMoveMode, cancelDamageMode;
    private final TeleportMode teleportMode;
    private final String mode;

    public TeleportTask(SST plugin, Player player, Location loc, TeleportMode teleportMode, TeleportSettings settings) {
        this.teleportManager = plugin.getTeleportManager();
        this.player = player;
        this.user = plugin.getUserManager().getUser(player);
        this.settings = settings;
        this.mode = teleportMode.getModeString();
        this.teleportMode = teleportMode;
        this.isStaff = player.hasPermission("sst.staff");

        this.time = isStaff && settings.doesStaffBypassTime()
                ? 0
                : settings.getTime();

        this.teleportingMode = MessageType.valueOf(Utils.getMessage("" + mode + ".teleporting.mode", player));
        this.teleportedMode = MessageType.valueOf(Utils.getMessage("" + mode +".teleported.mode", player));
        this.teleportingMessages = new TeleportMessage(player, user, mode, MessageMode.TELEPORTING);
        this.teleportedMessages = new TeleportMessage(player, user, mode, MessageMode.TELEPORTED);

        this.firstLoc = player.getLocation();
        this.finalLoc = loc;
        this.firstHealth = player.getHealth();

        this.cancelMoveMode = settings.getCancelMoveMode();
        this.cancelDamageMode = settings.getCancelDamageMode();

        switch (teleportMode.mode()) {
            case WARPS -> user.setState(UserState.TELEPORTING_WARP);
            case SPAWN -> user.setState(UserState.TELEPORTING_SPAWN);
            case HOMES -> user.setState(UserState.TELEPORTING_HOME);
            default -> user.setState(UserState.TELEPORTING_PLAYER);
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
            Utils.sendMessage(player, user, teleportedMode, teleportedMessages, teleportMode, String.valueOf(time));
            cancelTeleport(false);
            return;
        }

        Utils.sendMessage(player, user, teleportingMode, teleportingMessages, teleportMode, String.valueOf(time));
        time--;
    }

    public void cancelTeleport(boolean msg) {
        user.setState(UserState.PLAYING);
        if (msg) player.sendMessage(Utils.getMessage(mode + ".teleport-cancelled", player));
        teleportManager.removeTeleportTask(this);
        super.cancel();
    }

}