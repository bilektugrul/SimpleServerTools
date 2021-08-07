package io.github.bilektugrul.simpleservertools.users;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.homes.Home;
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
        String name = p.getName();
        UUID uuid = p.getUniqueId();
        YamlConfiguration dataFile = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder() + "/players/" + uuid + ".yml"));
        Set<Home> homes = new HashSet<>();
        for (String homeName : dataFile.getConfigurationSection("homes").getKeys(false)) {
            Home home = new Home(homeName, dataFile.getLocation("homes." + homeName + ".location"));
            homes.add(home);
        }
        User user = new User(dataFile, uuid, UserState.PLAYING, false, name, homes, plugin);
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
