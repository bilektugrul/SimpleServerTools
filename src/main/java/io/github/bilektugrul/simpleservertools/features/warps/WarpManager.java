package io.github.bilektugrul.simpleservertools.features.warps;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import me.despical.commons.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WarpManager {

    private final ArrayList<Warp> warpList = new ArrayList<>();
    private final SST plugin;
    private FileConfiguration warpsFile;
    private TeleportSettings settings;

    public WarpManager(SST plugin) {
        this.plugin = plugin;
        reloadWarps();
        loadSettings();
    }

    public boolean registerWarp(Warp warp) {
        return registerWarp(warp.getName(), warp.getLocation(), warp.getPermRequire());
    }

    public boolean registerWarp(String name, Location loc) {
        if (!isPresent(name)) {
            warpList.add(new Warp(name, loc, false));
            saveWarps();
            return true;
        }
        return false;
    }

    public boolean registerWarp(String name, Location loc, boolean permRequired) {
        if (!isPresent(name)) {
            warpList.add(new Warp(name, loc, permRequired));
            saveWarps();
            return true;
        }
        return false;
    }

    public void forceRegisterWarp(String name, Location loc) {
        warpList.add(new Warp(name, loc, false));
        saveWarps();
    }

    public void forceRegisterWarp(String name, Location loc, boolean permRequired) {
        warpList.add(new Warp(name, loc, permRequired));
        saveWarps();
    }

    public boolean deleteWarp(String name) {
        if (isPresent(name)) {
            warpList.removeIf(entry -> entry.getName().equalsIgnoreCase(name));
            saveWarps();
            return true;
        }
        return false;
    }

    public boolean deleteWarp(Warp warp) {
        return deleteWarp(warp.getName());
    }

    public boolean isPresent(String name) {
        for (Warp entry : warpList) {
            if (entry.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public void loadSettings() {
        final int time = Utils.getInt("warps.teleport-time");

        final boolean blockMove = Utils.getBoolean("warps.cancel-when-move.settings.block-move");
        final boolean blockCommands = Utils.getBoolean("warps.block-commands.enabled");
        final boolean cancelTeleportOnMove = Utils.getBoolean("warps.cancel-when-move.settings.cancel-teleport");
        final boolean blockDamage = Utils.getBoolean("warps.cancel-damage.settings.block-damage");
        final boolean cancelTeleportOnDamage = Utils.getBoolean("warps.cancel-damage.settings.cancel-teleport");
        final boolean staffBypassTime = Utils.getBoolean("warps.staff-bypass-time");

        final CancelMode cancelMoveMode = CancelMode.valueOf(Utils.getString("warps.cancel-when-move.mode", null));
        final CancelMode cancelDamageMode = CancelMode.valueOf(Utils.getString("warps.cancel-damage.mode", null));
        final CancelMode cancelCommandsMode = CancelMode.valueOf(Utils.getString("warps.block-commands.mode", null));

        settings = new TeleportSettings()
                .setTime(time)
                .setBlockMove(blockMove)
                .setBlockCommands(blockCommands)
                .setCancelTeleportOnMove(cancelTeleportOnMove)
                .setBlockDamage(blockDamage)
                .setCancelTeleportOnDamage(cancelTeleportOnDamage)
                .setStaffBypassTime(staffBypassTime)
                .setCancelMoveMode(cancelMoveMode)
                .setCancelDamageMode(cancelDamageMode)
                .setCancelCommandsMode(cancelCommandsMode);
    }

    public TeleportSettings getSettings() {
        if (settings == null) {
           loadSettings();
        }
        return settings;
    }

    public Warp getWarp(String name) {
        for (Warp entry : warpList) {
            if (entry.getName().equalsIgnoreCase(name)) {
                return entry;
            }
        }
        return null;
    }

    public void saveWarps() {
        for (Warp entry : warpList) {
            String name = entry.getName();

            warpsFile.set(("warps." + name + ".location"), entry.getLocation());
            warpsFile.set(("warps." + name + ".permissionRequired"), entry.getPermRequire());
        }
        ConfigUtils.saveConfig(plugin, warpsFile, "warps");
    }

    public void reloadWarps() {
        this.warpsFile = ConfigUtils.getConfig(plugin, "warps");
        loadWarps();
    }

    public String readableWarpList(CommandSender sender) {
        boolean isAdmin = sender.hasPermission("sst.admin");
        if (!warpList.isEmpty()) {
            List<String> warps = warpList.stream()
                    .filter(warp -> isAdmin || !warp.getPermRequire() || warp.getPermRequire() && sender.hasPermission(warp.getPermission()))
                    .map(Warp::getName)
                    .collect(Collectors.toList());
            return String.join(", ", warps);
        } else {
            return Utils.getMessage("warps.no-warp", null);
        }
    }

    public void sendWarpInfo(Warp warp, CommandSender sender) {
        if (!sender.hasPermission("sst.warpinfo")) return;
        sender.sendMessage(Utils.getMessage("warps.info", sender)
                .replace("%warp%", warp.getName())
                .replace("%warploc%", readableWarpLoc(warp))
                .replace("%warpperm%", warp.getPermRequire() ? warp.getPermission() : "yok"));
    }

    public ArrayList<Warp> getWarpList() {
        return warpList;
    }

    public String readableWarpLoc(String warp) {
        return readableWarpLoc(getWarp(warp));
    }

    public String readableWarpLoc(Warp warp) {
        return LocationSerializer.toString(warp.getLocation());
    }

    public void loadWarps() {
        warpList.clear();
        if (warpsFile.contains("warps")) {
            for (String name : warpsFile.getConfigurationSection("warps").getKeys(false)) {
                boolean permRequired = warpsFile.getBoolean("warps." + name + ".permissionRequired");
                Location location = (Location) warpsFile.get("warps." + name + ".location");
                warpList.add(new Warp(name, location, permRequired));
            }
        }
    }

}
