package io.github.bilektugrul.simpleservertools.users;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    public enum State {
        TELEPORTING, TELEPORTING_SPAWN, PLAYING
    }

    private final UUID uuid;
    private State state;
    private boolean isGod;

    public User(UUID uuid, State state) {
        this.uuid = uuid;
        this.state = state;
    }

    public User(UUID uuid, State state, boolean isGod) {
        this.uuid = uuid;
        this.state = state;
        this.isGod = isGod;
    }

    public UUID getUUID() {
        return uuid;
    }

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        state = newState;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public String getName() {
        return getPlayer().getName();
    }

    public boolean isGod() {
        return isGod;
    }

    public void setGod(boolean isGod) {
        this.isGod = isGod;
    }

}
