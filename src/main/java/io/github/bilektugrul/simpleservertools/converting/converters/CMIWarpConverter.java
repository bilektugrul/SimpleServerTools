package io.github.bilektugrul.simpleservertools.converting.converters;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.converting.Converter;
import io.github.bilektugrul.simpleservertools.converting.FinalState;
import io.github.bilektugrul.simpleservertools.features.warps.Warp;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CMIWarpConverter extends Converter {

    private final SST plugin;
    private final WarpManager warpManager;

    public CMIWarpConverter(SST plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public boolean canConvert() {
        return true;
    }

    @Override
    public FinalState convert() {
        Logger logger = plugin.getLogger();
        String s = File.separator;
        FileConfiguration cmiWarps = YamlConfiguration.loadConfiguration(new File("plugins" + s + "CMI" + s + "warps.yml"));

        if (cmiWarps.getKeys(true).isEmpty()) {
            logger.warning(ChatColor.RED + "Someone tried to convert CMI warps into SST warps, but there is no CMI warp. Don't mess with me.");
            return FinalState.UNSUCCESSFUL;
        }

        List<Warp> convertedWarps = new ArrayList<>();
        List<String> brokenWarps = new ArrayList<>();

        for (String key : cmiWarps.getKeys(false)) {
            String[] warpArray = cmiWarps.getString(key + ".Location").split(";");

            String worldName = warpArray[0];
            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                logger.warning(ChatColor.RED + "World " + worldName + " does not exist, CMI warp " + key + " will not be converted.");
                brokenWarps.add(key);
                continue;
            }

            double x = Double.parseDouble(warpArray[1]);
            double y = Double.parseDouble(warpArray[2]);
            double z = Double.parseDouble(warpArray[3]);
            float yaw = Float.parseFloat(warpArray[4]);
            float pitch = Float.parseFloat(warpArray[5]);

            Location loc = new Location(world, x, y, z, yaw, pitch);
            Warp converted = new Warp(key, loc, cmiWarps.getBoolean(key + ".ReqPerm", false));
            convertedWarps.add(converted);
        }

        List<Warp> convertedWarps2 = new ArrayList<>(convertedWarps);

        for (Warp converted : convertedWarps2) {
            if (!warpManager.registerWarp(converted)) {
                brokenWarps.add(converted.name());
                convertedWarps.remove(converted);
            }
        }

        if (brokenWarps.size() == cmiWarps.getKeys(false).size()) {
            logger.log(Level.WARNING, ChatColor.RED + "Everything went wrong. Sorry. Your CMI warps are broken or you have already converted them.");
            return FinalState.UNSUCCESSFUL;
        }

        FinalState state = FinalState.COMPLETED;

        if (brokenWarps.isEmpty()) {
            logger.info(ChatColor.GREEN + "All CMI warps has been converted. Congratulations. Never use it again.");
        } else {
            logger.warning(ChatColor.RED + "Some CMI warps couldn't be converted. Here is the list of them:");
            brokenWarps.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp));
            state = FinalState.ALMOST;
        }

        logger.info(ChatColor.GREEN + "Successfully converted and registered warps (" + convertedWarps.size() + "):");
        convertedWarps.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp.name()));
        return state;
    }

    @Override
    public String getName() {
        return "CMIWarpConverter";
    }

    @Override
    public String getAuthor() {
        return "bilektugrul";
    }

}
