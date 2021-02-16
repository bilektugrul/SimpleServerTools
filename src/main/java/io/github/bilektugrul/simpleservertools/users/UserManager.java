package io.github.bilektugrul.simpleservertools.users;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class UserManager {

    private final Set<User> userList = new HashSet<>();

    public User getUser(UUID uuid) {
        for (User user : userList) {
            if (user.getUUID().equals(uuid)) {
                return user;
            }
        }
        User user = new User(uuid, User.State.PLAYING);
        userList.add(user);
        return user;
    }

    public boolean isTeleporting(User user) {
        User.State state = user.getState();
        return state == User.State.TELEPORTING || state == User.State.TELEPORTING_SPAWN;
    }

    public boolean isTeleporting(UUID uuid) {
        User.State state = getUser(uuid).getState();
        if (isPresent(uuid)) return state == User.State.TELEPORTING || state == User.State.TELEPORTING_SPAWN;
        return false;
    }

    public User.State getState(User user) {
        return user.getState();
    }

    public User.State getState(UUID uuid) {
        return getUser(uuid).getState();
    }

    public void setState(User user, User.State newState) {
        user.setState(newState);
    }

    public void setState(UUID uuid, User.State newState) {
       if (isPresent(uuid)) getUser(uuid).setState(newState);
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

}
