package io.github.bilektugrul.simpleservertools.commands.speed;

import org.bukkit.entity.Player;

public class SpeedMode {

    private enum Mode {
        FLY, WALK
    }

    private Player player;
    private float speed;
    private Mode mode;

    public SpeedMode(Player player) {
        this.player = player;
        speed = getCurrentSpeed();
        mode = matchMode();
    }

    public Player getPlayer() {
        return player;
    }

    public float getSpeed() {
        return speed;
    }

    public Mode getMode() {
        return mode;
    }

    public void setPlayer(Player player) {
        this.player = player;
        mode = matchMode();
        speed = getCurrentSpeed();
    }

    public void setMode(Mode newMode) {
        mode = newMode;
        change();
    }

    public void setSpeed(float newSpeed) {
        speed = newSpeed;
        change();
    }

    public Mode matchMode() {
        return player.isFlying() ? Mode.FLY : Mode.WALK;

    }

    public float getCurrentSpeed() {
        return mode == Mode.WALK ? player.getWalkSpeed() : player.getFlySpeed();

    }

    private void change() {
        if (matchMode() == Mode.WALK) player.setWalkSpeed(speed);
        else player.setFlySpeed(speed);
    }

}
