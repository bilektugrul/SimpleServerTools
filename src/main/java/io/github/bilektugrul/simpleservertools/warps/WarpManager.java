package io.github.bilektugrul.simpleservertools.warps;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import me.despical.commonsbox.configuration.ConfigUtils;
import me.despical.commonsbox.serializer.LocationSerializer;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.*;
import java.util.stream.Collectors;

public class WarpManager {

    private final ArrayList<Warp> warpList = new ArrayList<>();
    private final SimpleServerTools plugin;
    private final FileConfiguration warpsFile;

    public WarpManager(SimpleServerTools plugin) {
        this.plugin = plugin;
        this.warpsFile = ConfigUtils.getConfig(plugin, "warps");
        loadWarps();
    }

    public boolean registerWarp(String name, Location loc) {
        if (!isPresent(name)) {
            warpList.add(new Warp(name, loc, "none", false));
            saveWarps();
            return true;
        }
        return false;
    }

    public boolean registerWarp(String name, Location loc, String permission) {
        if (!isPresent(name)) {
            warpList.add(new Warp(name, loc, permission, true));
            saveWarps();
            return true;
        }
        return false;
    }

    public void forceRegisterWarp(String name, Location loc) {
        warpList.add(new Warp(name, loc, "none", false));
        saveWarps();
    }

    public void forceRegisterWarp(String name, Location loc, String permission) {
        warpList.add(new Warp(name, loc, permission, true));
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
            warpsFile.set(("warps." + name + ".permission"), "sst." + entry.getPermission());
        }
        ConfigUtils.saveConfig(plugin, warpsFile, "warps");
    }

    public String readableWarpList() {
        if (!warpList.isEmpty()) {
            List<String> warps = warpList.stream().map(Warp::getName).collect(Collectors.toList());
            return String.join(", ", warps);
        } else {
            return "Yok";
        }
    }

    public ArrayList<Warp> getWarpList() {
        return warpList;
    }

    public String readableWarpLoc(String warp) {
        return LocationSerializer.locationToString(getWarp(warp).getLocation());
    }

    public void loadWarps() {
        if (warpsFile.contains("warps")) {
            for (String name : warpsFile.getConfigurationSection("warps").getKeys(false)) {
                String permission = warpsFile.getString("warps." + name + ".permission");
                Location location = (Location) warpsFile.get("warps." + name + ".location");
                warpList.add(new Warp(name, location, permission, !permission.equalsIgnoreCase("sst.none")));
            }
        }
    }

}
