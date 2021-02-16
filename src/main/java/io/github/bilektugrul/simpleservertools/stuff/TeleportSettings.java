package io.github.bilektugrul.simpleservertools.stuff;

public class TeleportSettings {

    private final int time;
    private final CancelModes cancelMoveMode, cancelDamageMode;
    private final boolean blockMove, cancelTeleportOnMove, blockDamage, cancelTeleportOnDamage, staffBypassTime;

    public TeleportSettings(int time, boolean blockMove, boolean cancelTeleportOnMove, CancelModes cancelMoveMode, boolean blockDamage, boolean cancelTeleportOnDamage, CancelModes cancelDamageMode, boolean staffBypassTime) {
        this.time = time;
        this.blockMove = blockMove;
        this.cancelTeleportOnMove = cancelTeleportOnMove;
        this.cancelMoveMode = cancelMoveMode;
        this.blockDamage = blockDamage;
        this.cancelTeleportOnDamage = cancelTeleportOnDamage;
        this.cancelDamageMode = cancelDamageMode;
        this.staffBypassTime = staffBypassTime;
    }

    public int getTime() {
        return time;
    }

    public boolean getBlockMove() {
        return blockMove;
    }

    public boolean getCancelTeleportOnMove() {
        return cancelTeleportOnMove;
    }

    public CancelModes getCancelMoveMode() {
        return cancelMoveMode;
    }

    public boolean getBlockDamage() {
        return blockDamage;
    }

    public boolean getCancelTeleportOnDamage() {
        return cancelTeleportOnDamage;
    }

    public CancelModes getCancelDamageMode() {
        return cancelDamageMode;
    }

    public boolean getStaffBypassTime() {
        return staffBypassTime;
    }

}
