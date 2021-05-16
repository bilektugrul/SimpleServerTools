package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.MessageType;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.users.UserState;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportTask extends BukkitRunnable {

    private final SST plugin = JavaPlugin.getPlugin(SST.class);
    private final TeleportManager teleportManager = plugin.getTeleportManager();

    private final Player p;

    private final TeleportSettings settings;
    private final boolean isStaff;
    private int time;
    private String teleportingMsg;
    private final MessageType teleportingMode;
    private String teleportingSub;
    private String teleportedMsg;
    private final MessageType teleportedMode;
    private String teleportedSub;
    private final Location firstLoc;
    private final Location finalLoc;
    private final double firstHealth;

    private final CancelMode cancelMoveMode;
    private final CancelMode cancelDamageMode;
    private final User user;
    private final String mode;

    private Sound countdownSound;
    private Sound teleportSound;
    private Sound cancelMoveSound;
    private Sound cancelDamageSound;
    private boolean soundsEnabled = false;

    public TeleportTask(Player player, Location loc, TeleportMode teleportMode, TeleportSettings teleportSettings) {
        p = player;
        settings = teleportSettings;
        mode = teleportMode.getModeString();
        isStaff = player.hasPermission("sst.staff");

        time = isStaff && settings.getStaffBypassTime()
                ? 0
                : settings.getTime();

        teleportingMsg = Utils.getMessage("" + mode + ".teleporting.message", player);
        teleportingMode = MessageType.valueOf(Utils.getMessage("" + mode + ".teleporting.mode", player));
        teleportingSub = "";

        teleportedMsg = Utils.getMessage("" + mode + ".teleported.message", player);
        teleportedMode = MessageType.valueOf(Utils.getMessage("" + mode +".teleported.mode", player));
        teleportedSub = "";

        firstLoc = player.getLocation();
        finalLoc = loc;
        firstHealth = player.getHealth();

        if(Utils.getBoolean("sounds.teleports." + mode + ".enabled")){
            countdownSound = Sound.valueOf(Utils.getString("sounds.teleports." + mode + ".countdown-sound", player));
            teleportSound = Sound.valueOf(Utils.getString("sounds.teleports." + mode + ".finish-sound", player));
            cancelMoveSound = Sound.valueOf(Utils.getString("sounds.teleports." + mode + ".cancel-move-sound", player));
            cancelDamageSound = Sound.valueOf(Utils.getString("sounds.teleports." + mode + ".cancel-damage-sound", player));
            soundsEnabled = true;
        }


        cancelMoveMode = settings.getCancelMoveMode();
        cancelDamageMode = settings.getCancelDamageMode();
        UserManager userManager = plugin.getUserManager();
        user = userManager.getUser(player);

        final Mode mode = teleportMode.getMode();

        if (mode == Mode.WARPS) {
            user.setState(UserState.TELEPORTING);
            String name = teleportMode.getWarp().getName();
            teleportedMsg = teleportedMsg.replace("%warp%", name);
            teleportingMsg = teleportingMsg.replace("%warp%", name);
        } else if (mode == Mode.SPAWN) {
            user.setState(UserState.TELEPORTING_SPAWN);
        } else {
            user.setState(UserState.TELEPORTING_PLAYER);
            String teleportingTo = teleportMode.getTPAInfo().getToTeleport().getName();
            teleportedMsg = teleportedMsg.replace("%teleporting%", teleportingTo);
            teleportingMsg = teleportingMsg.replace("%teleporting%", teleportingTo);
        }

        if ((teleportingMode == MessageType.TITLE && teleportingMsg.contains("\n"))) {
            int index = teleportingMsg.indexOf("\n");
            try {
                teleportingSub = teleportingMsg.split("\n")[1];
            } catch (ArrayIndexOutOfBoundsException ignored) {
                teleportingSub = "";
            }
            teleportingMsg = teleportingMsg.substring(0, index);
        }
        if (teleportedMode == MessageType.TITLE && teleportedMsg.contains("\n")) {
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
        if (!Utils.isSameLoc(firstLoc, p.getLocation())) {
            boolean cancel = cancelMoveMode == CancelMode.EVERYONE
                    || (cancelMoveMode == CancelMode.STAFF && isStaff)
                    || (cancelMoveMode == CancelMode.EXCEPT_STAFF && !isStaff);
            if (cancel) {
                if (settings.getCancelTeleportOnMove()) {
                    cancelTeleport(true);
                    if(soundsEnabled) p.playSound(p.getLocation(), cancelMoveSound, 1,0);
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
                if (settings.getCancelTeleportOnDamage()) {
                    cancelTeleport(true);
                    if(soundsEnabled) p.playSound(p.getLocation(), cancelDamageSound, 1,0);
                }
            }
        }

        if (time == 0) {
            user.setState(UserState.PLAYING);
            PaperLib.teleportAsync(p, finalLoc, PlayerTeleportEvent.TeleportCause.PLUGIN);
            Utils.sendMessage(p, teleportedMode, teleportedMsg, teleportedSub, String.valueOf(time));
            if(soundsEnabled) p.playSound(p.getLocation(), teleportSound, 1,0);
            cancelTeleport(false);
            return;
        }

        Utils.sendMessage(p, teleportingMode, teleportingMsg, teleportingSub, String.valueOf(time));
        if(soundsEnabled) p.playSound(p.getLocation(), countdownSound, 1,0);
        time--;
    }

    public void cancelTeleport(boolean msg) {
        user.setState(UserState.PLAYING);
        if (msg) p.sendMessage(Utils.getMessage("" + mode + ".teleport-cancelled", p));
        teleportManager.getTeleportTasks().remove(this);
        super.cancel();
    }

}
