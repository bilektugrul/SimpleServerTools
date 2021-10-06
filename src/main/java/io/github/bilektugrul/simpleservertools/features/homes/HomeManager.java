package io.github.bilektugrul.simpleservertools.features.homes;

import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.utils.Utils;

public class HomeManager {
    
    private int defaultMaxHomeAmount;
    private TeleportSettings settings;

    public HomeManager() {
        reload();
    }

    public int getDefaultMaxHomeAmount() {
        return defaultMaxHomeAmount;
    }

    public void loadSettings() {
        int time = Utils.getInt("homes.teleport-time");

        boolean blockMove = Utils.getBoolean("homes.cancel-when-move.settings.block-move");
        boolean blockCommands = Utils.getBoolean("homes.block-commands.enabled");
        boolean cancelTeleportOnMove = Utils.getBoolean("homes.cancel-when-move.settings.cancel-teleport");
        boolean blockDamage = Utils.getBoolean("homes.cancel-damage.settings.block-damage");
        boolean cancelTeleportOnDamage = Utils.getBoolean("homes.cancel-damage.settings.cancel-teleport");
        boolean staffBypassTime = Utils.getBoolean("homes.staff-bypass-time");

        CancelMode cancelMoveMode = CancelMode.valueOf(Utils.getString("homes.cancel-when-move.mode"));
        CancelMode cancelDamageMode = CancelMode.valueOf(Utils.getString("homes.cancel-damage.mode"));
        CancelMode cancelCommandsMode = CancelMode.valueOf(Utils.getString("homes.block-commands.mode"));

        settings = new TeleportSettings()
                .setTime(time)
                .setBlockMove(blockMove)
                .setBlockCommands(blockCommands)
                .setCancelTeleportOnMove(cancelTeleportOnMove)
                .setBlockDamage(blockDamage)
                .setCancelTeleportOnDamage(cancelTeleportOnDamage)
                .setStaffBypassTime(staffBypassTime)
                .setCancelMoveMode(cancelMoveMode)
                .setCancelDamageMode(cancelDamageMode)
                .setCancelCommandsMode(cancelCommandsMode);
    }

    public TeleportSettings getSettings() {
        if (settings == null) {
            loadSettings();
        }
        return settings;
    }

    public void reload() {
        defaultMaxHomeAmount = Utils.getInt("homes.default-max-home-amount");
        loadSettings();
    }

}