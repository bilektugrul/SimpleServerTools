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
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EssentialsWarpConverter implements Converter {

    private final SST plugin;
    private final WarpManager warpManager;

    public EssentialsWarpConverter(SST plugin) {
        this.plugin = plugin;
        this.warpManager = plugin.getWarpManager();
    }

    @Override
    public FinalState convert() {
        Logger logger = plugin.getLogger();
        String s = File.separator;
        File[] essWarps = new File("plugins" + s + "Essentials" + s + "warps" + s).listFiles();

        if (essWarps == null) {
            logger.warning(ChatColor.RED + "Someone tried to convert Essentials warps into SST warps, but there is no Essentials warp. Don't mess with me.");
            return FinalState.UNSUCCESSFUL;
        }

        List<Warp> convertedWarps = new ArrayList<>();
        List<String> brokenWarps = new ArrayList<>();

        for (File file : essWarps) {
            String fileName = file.getName();
            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

                String worldName = yaml.getString("world");
                World world = Bukkit.getWorld(worldName);
                if (world == null) {
                    logger.warning(ChatColor.RED + "World " + worldName + " does not exist, Essentials warp file " + fileName + " will not be converted.");
                    brokenWarps.add(fileName);
                    continue;
                }

                double x = yaml.getDouble("x");
                double y = yaml.getDouble("y");
                double z = yaml.getDouble("z");
                float yaw = getFloat(yaml, "yaw");
                float pitch = getFloat(yaml, "pitch");
                String name = yaml.getString("name");

                Location loc = new Location(world, x, y, z, yaw, pitch);

                Warp converted = new Warp(name, loc, true);

                convertedWarps.add(converted);
            } catch (Exception ex) {
                logger.warning(ChatColor.RED + "Something went wrong while converting Essentials warp file " + fileName + ". Error:");
                ex.printStackTrace();
            }
        }
        
        List<Warp> convertedWarps2 = new ArrayList<>(convertedWarps);

        for (Warp converted : convertedWarps2) {
            if (!warpManager.registerWarp(converted)) {
                brokenWarps.add(converted.getName());
                convertedWarps.remove(converted);
            }
        }

        if (brokenWarps.size() == essWarps.length) {
            logger.log(Level.WARNING, ChatColor.RED + "Everything went wrong. Sorry. Your Essentials warps files are broken or you have already converted them.");
            return FinalState.UNSUCCESSFUL;
        }

        FinalState state = FinalState.COMPLETED;

        if (brokenWarps.isEmpty()) {
            logger.info(ChatColor.GREEN + "All Essentials warps has been converted. Congratulations. Never use it again.");
        } else {
            logger.warning(ChatColor.RED + "Some Essentials warps couldn't be converted. Here is the list of them:");
            brokenWarps.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp));
            state = FinalState.ALMOST;
        }

        logger.info(ChatColor.GREEN + "Succesfully converted and registered warps (" + convertedWarps.size() + "):");
        convertedWarps.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp.getName()));
        return state;
    }

    @Override
    public String getName() {
        return "EssentialsWarpConverter";
    }

    @Override
    public String getAuthor() {
        return "bilektugrul";
    }

    public float getFloat(YamlConfiguration yaml, String path) {
        return Float.parseFloat(yaml.getString(path));
    }

}
