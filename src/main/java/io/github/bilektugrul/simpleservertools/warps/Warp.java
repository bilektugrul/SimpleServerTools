package io.github.bilektugrul.simpleservertools.warps;

import org.bukkit.Location;

public class Warp {

    private final String warp;
    private final Location location;
    private final String permission;
    private final boolean permRequired;

    public Warp(String warp, Location location, String permission, boolean permRequired) {
        this.warp = warp;
        this.location = location;
        this.permission = permission;
        this.permRequired = permRequired;
    }

    public String getName() {
        return warp;
    }

    public Location getLocation() {
        return location;
    }

    public String getPermission() {
        return permission;
    }

    public boolean getPermRequire() {
        return permRequired;
    }
}
