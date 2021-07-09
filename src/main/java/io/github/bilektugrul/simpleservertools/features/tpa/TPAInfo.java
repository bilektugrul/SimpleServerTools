package io.github.bilektugrul.simpleservertools.features.tpa;

import org.bukkit.entity.Player;

public record TPAInfo(Player teleportingPlayer, Player toTeleport) {

    public Player getTeleportingPlayer() {
        return teleportingPlayer;
    }

    public Player getToTeleport() {
        return toTeleport;
    }

}
