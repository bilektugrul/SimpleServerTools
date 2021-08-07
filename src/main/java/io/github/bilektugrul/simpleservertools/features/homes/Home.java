package io.github.bilektugrul.simpleservertools.features.homes;

import org.bukkit.Location;

public record Home(String name, Location location) {

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

}
