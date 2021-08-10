package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SST;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final SST plugin;
    private final Set<User> userList = new HashSet<>();

    public UserManager(SST plugin) {
        this.plugin = plugin;
    }

    public User loadUser(Player p) {
        return loadUser(p.getUniqueId(), true);
    }

    public User loadUser(UUID uuid, boolean keep) {
        YamlConfiguration dataFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + uuid + ".yml"));
        User user = new User(dataFile, uuid, plugin);
        if (keep) userList.add(user);
        return user;
    }

    public User getUser(Player p) {
        UUID uuid = p.getUniqueId();
        for (User user : userList) {
            if (user.getUUID().equals(uuid)) {
                return user;
            }
        }
        return loadUser(uuid, true);
    }

    public boolean isTeleporting(User user) {
        UserState state = user.getState();
        return state == UserState.TELEPORTING || state == UserState.TELEPORTING_SPAWN || state == UserState.TELEPORTING_PLAYER;
    }

    public boolean isTeleporting(Player player) {
        UUID uuid = player.getUniqueId();
        if (isPresent(uuid)) {
            return isTeleporting(getUser(player));
        }
        return false;
    }

    public Set<User> getUserList() {
        return userList;
    }

    public boolean isPresent(UUID uuid) {
        for (User user : userList) {
            if (user.getUUID().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public void saveUsers() throws IOException {
        for (User user : userList) {
            user.save();
        }
    }

}
