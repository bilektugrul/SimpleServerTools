package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.features.homes.Home;
import io.github.bilektugrul.simpleservertools.features.spawn.Spawn;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAInfo;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;

import java.util.Locale;

public record TeleportMode(Mode mode, Warp warp, Spawn spawn, Home home, TPAInfo tpaInfo) {

    public TeleportMode(Mode mode, Warp warp) {
        this(mode, warp, null, null, null);
    }

    public TeleportMode(Mode mode, Spawn spawn) {
        this(mode, null, spawn, null, null);
    }

    public TeleportMode(Mode mode, Home home) {
        this(mode, null, null, home, null);
    }

    public TeleportMode(Mode mode, TPAInfo tpaInfo) {
        this(mode, null, null, null, tpaInfo);
    }

    public String getModeString() {
        return mode.toString().toLowerCase(Locale.ROOT);
    }

}