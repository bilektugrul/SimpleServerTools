package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.Home;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class User {

    private final SST plugin;
    private final UUID uuid;
    private final String name;
    private UserState state;
    private boolean isGod;
    private boolean isAfk;
    private final YamlConfiguration data;

    private final List<String> tpaBlockedPlayers = new ArrayList<>();
    private final List<String> msgBlockedPlayers = new ArrayList<>();
    private final Set<Home> homes = new HashSet<>();

    public User(YamlConfiguration data, UUID uuid, String name, SST plugin) {
        this.data = data;
        this.uuid = uuid;
        this.name = name;
        this.state = UserState.PLAYING;

        data.set("lastKnownName", name);
        if (!data.contains("accepting-tpa")) data.set("accepting-tpa", true);
        if (!data.contains("accepting-msg")) data.set("accepting-msg", true);
        tpaBlockedPlayers.addAll(data.getStringList("tpa-blocked-players"));
        msgBlockedPlayers.addAll(data.getStringList("msg-blocked-players"));
        if (data.isConfigurationSection("homes")) {
            for (String homeName : data.getConfigurationSection("homes").getKeys(false)) {
                Home home = new Home(homeName, Utils.getLocation(data,"homes." + homeName + ".location"));
                homes.add(home);
            }
        }

        this.plugin = plugin;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public UserState getState() {
        return state;
    }

    public void setState(UserState newState) {
        state = newState;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean isGod() {
        return isGod;
    }

    public boolean isAfk() {
        return isAfk;
    }

    public List<String> getMsgBlockedPlayers() {
        return new ArrayList<>(msgBlockedPlayers);
    }

    public List<String> getTPABlockedPlayers() {
        return new ArrayList<>(tpaBlockedPlayers);
    }

    public Set<Home> getHomes() {
        return new HashSet<>(homes);
    }

    public Home getHomeByName(String name) {
        for (Home home : homes) {
            if (home.name().equalsIgnoreCase(name)) {
                return home;
            }
        }
        return null;
    }

    public boolean createHome(Home home) {
        if (getHomeByName(home.name()) == null) {
            homes.add(home);
            return true;
        }
        return false;
    }

    public void deleteHome(String name) {
        homes.removeIf(home -> home.name().equalsIgnoreCase(name));
        data.set("homes." + name, null);
    }

    public String readableHomeList() {
        if (!homes.isEmpty()) {
            List<String> homes = this.homes.stream()
                    .map(Home::name)
                    .collect(Collectors.toList());
            return String.join(", ", homes);
        } else {
            return Utils.getMessage("homes.no-home", null);
        }
    }

    public boolean toggleTPABlock(String name) {
        if (tpaBlockedPlayers.contains(name)) {
            tpaBlockedPlayers.remove(name);
            return false;
        } else {
            tpaBlockedPlayers.add(name);
            return true;
        }
    }

    public boolean toggleMsgBlock(String name) {
        if (msgBlockedPlayers.contains(name)) {
            msgBlockedPlayers.remove(name);
            return false;
        } else {
            msgBlockedPlayers.add(name);
            return true;
        }
    }

    public void setGod(boolean isGod) {
        this.isGod = isGod;
    }

    public void setAfk(boolean isAfk) {
        this.isAfk = isAfk;
    }

    public boolean isAvailable() {
        return state == UserState.PLAYING;
    }

    public boolean isAcceptingTPA() {
        return data.getBoolean("accepting-tpa");
    }

    public boolean isBlockedTPAsFrom(String name) {
        return tpaBlockedPlayers.contains(name);
    }

    public void setAcceptingTPA(boolean acceptingTPA) {
        data.set("accepting-tpa", acceptingTPA);
    }

    public boolean isAcceptingMsg() {
        return data.getBoolean("accepting-msg");
    }

    public boolean isBlockedMsgsFrom(String name) {
        return msgBlockedPlayers.contains(name);
    }

    public void setAcceptingMsg(boolean acceptingMsg) {
        data.set("accepting-msg", acceptingMsg);
    }

    public void save() throws IOException {
        data.set("msg-blocked-players", msgBlockedPlayers);
        data.set("tpa-blocked-players", tpaBlockedPlayers);
        for (Home home : homes) {
            data.set("homes." + home.name() + ".location", home.location());
        }
        data.save(new File(plugin.getDataFolder() + "/players/" + uuid + ".yml"));
    }

}
