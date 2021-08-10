package io.github.bilektugrul.simpleservertools.features.warps;

import org.bukkit.Location;

public record Warp(String name, Location location, boolean permRequired) {

    public String getPermission() {
        return "sst.warps." + name;
    }

}
