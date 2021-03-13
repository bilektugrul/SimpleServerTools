package io.github.bilektugrul.simpleservertools.features.tpa;

import org.bukkit.entity.Player;

public class TPAInfo {

    private final Player teleportingPlayer;
    private final Player toTeleport;

    public TPAInfo(Player teleportingPlayer, Player toTeleport) {
        this.teleportingPlayer = teleportingPlayer;
        this.toTeleport = toTeleport;
    }

    public Player getTeleportingPlayer() {
        return teleportingPlayer;
    }

    public Player getToTeleport() {
        return toTeleport;
    }

}
