package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.stuff.CancelMode;

public class TeleportSettings {

    private int time;
    private CancelMode cancelMoveMode, cancelDamageMode, cancelCommandsMode;
    private boolean blockMove, cancelTeleportOnMove, blockDamage, cancelTeleportOnDamage, staffBypassTime, blockCommands;

    public TeleportSettings setTime(int time) {
        this.time = time;
        return this;
    }

    public TeleportSettings setCancelMoveMode(CancelMode cancelMoveMode) {
        this.cancelMoveMode = cancelMoveMode;
        return this;
    }

    public TeleportSettings setCancelDamageMode(CancelMode cancelDamageMode) {
        this.cancelDamageMode = cancelDamageMode;
        return this;
    }

    public TeleportSettings setCancelCommandsMode(CancelMode cancelCommandsMode) {
        this.cancelCommandsMode = cancelCommandsMode;
        return this;
    }

    public TeleportSettings setBlockMove(boolean blockMove) {
        this.blockMove = blockMove;
        return this;
    }

    public TeleportSettings setCancelTeleportOnMove(boolean cancelTeleportOnMove) {
        this.cancelTeleportOnMove = cancelTeleportOnMove;
        return this;
    }

    public TeleportSettings setBlockDamage(boolean blockDamage) {
        this.blockDamage = blockDamage;
        return this;
    }

    public TeleportSettings setCancelTeleportOnDamage(boolean cancelTeleportOnDamage) {
        this.cancelTeleportOnDamage = cancelTeleportOnDamage;
        return this;
    }

    public TeleportSettings setStaffBypassTime(boolean staffBypassTime) {
        this.staffBypassTime = staffBypassTime;
        return this;
    }

    public TeleportSettings setBlockCommands(boolean blockCommands) {
        this.blockCommands = blockCommands;
        return this;
    }

    public int getTime() {
        return time;
    }

    public boolean doesBlockMove() {
        return blockMove;
    }

    public boolean doesCancelTeleportOnMove() {
        return cancelTeleportOnMove;
    }

    public CancelMode getCancelMoveMode() {
        return cancelMoveMode;
    }

    public boolean doesBlockDamage() {
        return blockDamage;
    }

    public boolean doesCancelTeleportOnDamage() {
        return cancelTeleportOnDamage;
    }

    public CancelMode getCancelDamageMode() {
        return cancelDamageMode;
    }

    public boolean doesStaffBypassTime() {
        return staffBypassTime;
    }

    public boolean doesBlockCommands() {
        return blockCommands;
    }

    public CancelMode getCancelCommandsMode() {
        return cancelCommandsMode;
    }

}
