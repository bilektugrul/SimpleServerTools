package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SST;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class UserManager {

    private final SST plugin;
    private final Map<UUID, User> cachedUsers = new HashMap<>();

    public UserManager(SST plugin) {
        this.plugin = plugin;
    }

    public User loadUser(Player p) {
        return loadUser(p.getUniqueId(), p.getName(), true);
    }

    public User loadUser(UUID uuid, String name, boolean keep) {
        YamlConfiguration dataFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + uuid + ".yml"));
        User user = new User(dataFile, uuid, name);
        if (keep) cachedUsers.put(uuid, user);
        return user;
    }

    public User getUser(Player p) {
        UUID uuid = p.getUniqueId();
        return getUser(uuid);
    }

    public User getUser(UUID uuid) {
        return cachedUsers.get(uuid);
    }

    public void removeUser(User user) {
        cachedUsers.remove(user.getUUID());
    }

    public boolean isTeleporting(User user) {
        UserState state = user.getState();
        return state == UserState.TELEPORTING_WARP || state == UserState.TELEPORTING_SPAWN || state == UserState.TELEPORTING_PLAYER || state == UserState.TELEPORTING_HOME;
    }

    public boolean isTeleporting(Player player) {
        User user = getUser(player);
        if (user != null) {
            return isTeleporting(user);
        }
        return false;
    }

    public Set<User> getAllUsers() {
        return new HashSet<>(cachedUsers.values());
    }

    public void saveUsers() throws IOException {
        for (User user : cachedUsers.values()) {
            user.save();
        }
    }

}