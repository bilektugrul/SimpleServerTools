package io.github.bilektugrul.simpleservertools.features.warps;

import org.bukkit.Location;

public record Warp(String warp, Location location, boolean permRequired) {

    public String getName() {
        return warp;
    }

    public Location getLocation() {
        return location;
    }

    public boolean getPermRequire() {
        return permRequired;
    }

    public String getPermission() {
        return "sst.warps." + warp;
    }

}
