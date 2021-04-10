package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class User {

    public enum State {
        TELEPORTING, TELEPORTING_SPAWN, TELEPORTING_PLAYER, PLAYING
    }

    private final static SimpleServerTools plugin = JavaPlugin.getPlugin(SimpleServerTools.class);
    private final UUID uuid;
    private State state;
    private boolean isGod;
    private final YamlConfiguration data;
    private final String name;

    public User(YamlConfiguration data, UUID uuid, State state, boolean isGod, String name) {
        this.uuid = uuid;
        this.state = state;
        this.isGod = isGod;
        this.data = data;
        if (!data.contains("accepting-tpa")) data.set("accepting-tpa", true);
        if (!data.contains("accepting-msg")) data.set("accepting-msg", true);
        this.name = name;
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

    public boolean isAvailable() {
        return state == State.PLAYING;
    }

    public boolean isAcceptingTPA() {
        return data.getBoolean("accepting-tpa");
    }

    public void setAcceptingTPA(boolean acceptingTPA) {
        data.set("accepting-tpa", acceptingTPA);
    }

    public boolean isAcceptingMsg() {
        return data.getBoolean("accepting-msg");
    }

    public void setAcceptingMsg(boolean acceptingMsg) {
        data.set("accepting-msg", acceptingMsg);
    }

    public void save() throws IOException {
        data.save(new File(plugin.getDataFolder() + "/players/" + name + ".yml"));
    }

}
