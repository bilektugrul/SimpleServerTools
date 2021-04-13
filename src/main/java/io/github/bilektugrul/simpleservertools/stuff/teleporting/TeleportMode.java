package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.features.spawn.Spawn;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAInfo;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;

import java.util.Locale;

public class TeleportMode {

    public enum Mode {
        SPAWN, WARPS, TPA
    }

    private final Mode mode;
    private final Warp warp;
    private final Spawn spawn;
    private final TPAInfo tpaInfo;

    public TeleportMode(Mode mode, Warp warp, Spawn spawn, TPAInfo tpaInfo) {
        this.mode = mode;
        this.warp = warp;
        this.spawn = spawn;
        this.tpaInfo = tpaInfo;
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

    public TPAInfo getTPAInfo() {
        return tpaInfo;
    }

}