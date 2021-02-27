package io.github.bilektugrul.simpleservertools.users;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class User {

    private final UUID uuid;
    private State state;

    public User(UUID uuid, State state) {
        this.uuid = uuid;
        this.state = state;
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

    public enum State {
        TELEPORTING, TELEPORTING_SPAWN, PLAYING
    }

}