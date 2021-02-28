package io.github.bilektugrul.simpleservertools.stuff;

import io.github.bilektugrul.simpleservertools.features.spawn.Spawn;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;

import java.util.Locale;

public class TeleportMode {

    public enum Mode {
        SPAWN, WARPS
    }

    private final Mode mode;
    private final Warp warp;
    private final Spawn spawn;

    public TeleportMode(Mode mode, Warp warp, Spawn spawn) {
        this.mode = mode;
        this.warp = warp;
        this.spawn = spawn;
    }

    public Mode getMode() {
        return mode;
    }

    public String getModeString() {
        return mode.toString().toLowerCase(Locale.ROOT);
    }

    public Warp getWarp() {
        return warp;
    }

    public Spawn getSpawn() {
        return spawn;
    }

}