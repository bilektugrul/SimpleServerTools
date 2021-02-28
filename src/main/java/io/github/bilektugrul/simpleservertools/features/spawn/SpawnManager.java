package io.github.bilektugrul.simpleservertools.features.spawn;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.stuff.CancelModes;
import io.github.bilektugrul.simpleservertools.stuff.TeleportMode;
import io.github.bilektugrul.simpleservertools.stuff.TeleportSettings;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import me.despical.commonsbox.configuration.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.logging.Level;

public class SpawnManager {

    private Spawn spawn;
    private final SimpleServerTools plugin;
    private FileConfiguration spawnFile;

    public SpawnManager(SimpleServerTools plugin) {
        this.plugin = plugin;
        reloadSpawn();
    }

    public Spawn getSpawn() {
        return spawn;
    }

    public void setSpawn(Location loc) {
        if (!isPresent()) {
            spawn = new Spawn(loc, getPermRequired(), getSettings());
        } else {
            spawn = new Spawn(loc, spawn.getPermRequire(), spawn.getSettings());
        }
        saveSpawn();
    }

    public void loadSpawn(boolean force) {
        Location loc = null;
        try {
            loc = (Location) spawnFile.get("spawn.location");
        } catch (NullPointerException ignored) {
            plugin.getLogger().log(Level.WARNING, "Spawn location doesn't exist, spawn will not be created.");
        }
        if (loc != null) {
            if (!force) {
                setSpawn(loc);
            } else {
                spawn = null;
                setSpawn(loc);
            }
        }
    }

    public void reloadSpawn() {
        this.spawnFile = ConfigUtils.getConfig(plugin, "spawn");
        loadSpawn(true);
    }

    public TeleportSettings getSettings() {
        if (!isPresent()) {
            final int time = spawnFile.getInt("spawn.command.teleport.time");
            final boolean blockMove = spawnFile.getBoolean("spawn.command.teleport.cancel-when-move.settings.block-move");
            final boolean cancelTeleportOnMove = spawnFile.getBoolean("spawn.command.teleport.cancel-when-move.settings.cancel-teleport");
            final CancelModes cancelMoveMode = CancelModes.valueOf(spawnFile.getString("spawn.command.teleport.cancel-when-move.mode"));
            final boolean blockDamage = spawnFile.getBoolean("spawn.command.teleport.cancel-damage.settings.block-damage");
            final boolean cancelTeleportOnDamage = spawnFile.getBoolean("spawn.command.teleport.cancel-damage.settings.cancel-teleport");
            final CancelModes cancelDamageMode = CancelModes.valueOf(spawnFile.getString("spawn.command.teleport.cancel-damage.mode"));
            final boolean staffBypassTime = spawnFile.getBoolean("spawn.command.teleport.staff-bypass-time");
            return new TeleportSettings(time, blockMove, cancelTeleportOnMove, cancelMoveMode, blockDamage, cancelTeleportOnDamage, cancelDamageMode, staffBypassTime);
        }
        return spawn.getSettings();
    }

    public void saveSpawn() {
        if (spawn != null) {
            plugin.getLogger().info("Saving spawn location.");
            spawnFile.set("spawn.location", spawn.getLocation());
            ConfigUtils.saveConfig(plugin, spawnFile, "spawn");
        }
    }

    public boolean getPermRequired() {
        return spawn == null
                ? spawnFile.getBoolean("spawn.command.permission-required")
                : spawn.getPermRequire();
    }

    public boolean isPresent() {
        return spawn != null;
    }

    public boolean isEnabled() {
        return spawnFile.getBoolean("spawn.enabled");
    }

    public void teleport(Player player, boolean direct) {
        if (isEnabled() && isPresent()) {
            Location loc = spawn.getLocation();
            loc.getChunk().load();
            TeleportMode mode = new TeleportMode(TeleportMode.Mode.SPAWN, null, spawn);
            if (!direct) {
                Utils.teleport(player, loc, mode);
            } else {
                //player.teleport(loc);
                PaperLib.teleportAsync(player, loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
                //Bukkit.getScheduler().runTask(plugin, () -> player.teleport(loc));
            }
        }
    }

    public void sendWarnIfEnabled(CommandSender sender) {
        if (spawnFile.getBoolean("spawn.send-warning-if-not-enabled"))
            sender.sendMessage(Utils.getString("other-messages.spawn.not-enabled", sender));
    }

    public FileConfiguration getSpawnFile() {
        return spawnFile;
    }


}
