package io.github.bilektugrul.simpleservertools.commands.speed;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.entity.Player;

public class SpeedInfo {

    private Player player;
    private float speed;
    private SpeedMode mode;

    public SpeedInfo(Player player) {
        this.player = player;
        update();
    }

    public Player getPlayer() {
        return player;
    }

    public float getSpeed() {
        return speed;
    }

    public SpeedMode getMode() {
        return mode;
    }

    public void setPlayer(Player player) {
        this.player = player;
        update();
    }

    public void setMode(SpeedMode newMode) {
        mode = newMode;
    }

    public void setSpeed(float newSpeed) {
        if (newSpeed > 1) newSpeed = 1;
        speed = newSpeed;
    }

    public SpeedMode matchMode() {
        return player.isFlying() ? SpeedMode.FLY : SpeedMode.WALK;
    }

    public float getCurrentSpeed() {
        return mode == SpeedMode.WALK ? player.getWalkSpeed() : player.getFlySpeed();
    }

    public void update() {
        speed = getCurrentSpeed();
        mode = matchMode();
    }

    public void apply() {
        if (matchMode() == SpeedMode.WALK) player.setWalkSpeed(speed);
        else player.setFlySpeed(speed);
        player.sendMessage(Utils.getString("other-messages.speed.changed", player)
                .replace("%mode%", Utils.getString("other-messages.speed.modes." + mode.name(), player))
                .replace("%newSpeed%", String.valueOf(speed)));
    }

}
