package io.github.bilektugrul.simpleservertools.features.tpa;

import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.utils.Utils;

public class TPAManager {

    private TeleportSettings settings;

    public boolean isTeleporting(User user) {
        return user.getState() == User.State.TELEPORTING_PLAYER;
    }
    
    public TeleportSettings getSettings() {
        if (settings == null) {
            final int time = Utils.getInt("tpa.teleport-time");
            final boolean blockMove = Utils.getBoolean("tpa.cancel-when-move.settings.block-move");
            final boolean cancelTeleportOnMove = Utils.getBoolean("tpa.cancel-when-move.settings.cancel-teleport");
            final CancelMode cancelMoveMode = CancelMode.valueOf(Utils.getString("tpa.cancel-when-move.mode", null));
            final boolean blockDamage = Utils.getBoolean("tpa.cancel-damage.settings.block-damage");
            final boolean cancelTeleportOnDamage = Utils.getBoolean("tpa.cancel-damage.settings.cancel-teleport");
            final CancelMode cancelDamageMode = CancelMode.valueOf(Utils.getString("tpa.cancel-damage.mode", null));
            final boolean staffBypassTime = Utils.getBoolean("tpa.staff-bypass-time");
            settings = new TeleportSettings(time, blockMove, cancelTeleportOnMove, cancelMoveMode, blockDamage, cancelTeleportOnDamage, cancelDamageMode, staffBypassTime);
        }
        return settings;
    }

}
