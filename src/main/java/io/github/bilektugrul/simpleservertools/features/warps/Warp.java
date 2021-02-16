package io.github.bilektugrul.simpleservertools.features.warps;

import org.bukkit.Location;

public class Warp {

    private final String warp;
    private final Location location;
    private final boolean permRequired;

    public Warp(String warp, Location location, boolean permRequired) {
        this.warp = warp;
        this.location = location;
        this.permRequired = permRequired;
    }

    public String getName() {
        return warp;
    }

    public Location getLocation() {
        return location;
    }

    public boolean getPermRequire() {
        return permRequired;
    }

}
