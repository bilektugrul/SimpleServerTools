package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final SimpleServerTools plugin;
    private final Set<User> userList = new HashSet<>();

    public UserManager(SimpleServerTools plugin) {
        this.plugin = plugin;
    }

    public User loadUser(Player p) {
        String name = p.getName();
        UUID uuid = p.getUniqueId();
        YamlConfiguration dataFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + name + ".yml"));
        User user = new User(dataFile, uuid, User.State.PLAYING, false, name);
        userList.add(user);
        return user;
    }

    public User getUser(Player p) {
        UUID uuid = p.getUniqueId();
        for (User user : userList) {
            if (user.getUUID().equals(uuid)) {
                return user;
            }
        }
        return loadUser(p);
    }

    public boolean isTeleporting(User user) {
        User.State state = user.getState();
        return state == User.State.TELEPORTING || state == User.State.TELEPORTING_SPAWN || state == User.State.TELEPORTING_PLAYER;
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
