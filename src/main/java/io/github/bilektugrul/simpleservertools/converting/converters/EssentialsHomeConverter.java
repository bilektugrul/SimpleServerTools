package io.github.bilektugrul.simpleservertools.converting.converters;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.converting.Converter;
import io.github.bilektugrul.simpleservertools.converting.FinalState;
import io.github.bilektugrul.simpleservertools.features.homes.Home;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public class EssentialsHomeConverter implements Converter {

    private final SST plugin;
    private final UserManager userManager;
    private final MaintenanceManager maintenanceManager;

    public EssentialsHomeConverter(SST plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.maintenanceManager = plugin.getMaintenanceManager();
    }

    @Override
    public boolean canConvert() {
        return maintenanceManager.isInMaintenance() && plugin.getServer().getOnlinePlayers().size() == 0;
    }

    @Override
    public FinalState convert() {
        Logger logger = plugin.getLogger();

        if (!canConvert()) {
            logger.warning(ChatColor.RED + "Home convert process can not be started while there are players online and server is not in maintenance mode.");
            return FinalState.UNSUCCESSFUL;
        }

        String s = File.separator;
        File[] essUsers = new File("plugins" + s + "Essentials" + s + "userdata" + s).listFiles();

        if (essUsers == null) {
            logger.warning(ChatColor.RED + "Someone tried to convert Essentials user homes into SST user homes, but there is no Essentials user data. Don't mess with me.");
            return FinalState.UNSUCCESSFUL;
        }

        for (File file : essUsers) {
            List<Home> convertedHomes = new ArrayList<>();
            List<String> brokenHomes = new ArrayList<>();

            String fileName = file.getName();
            String uuid = fileName.replace(".yml", "");
            User user = userManager.loadUser(UUID.fromString(uuid), false);

            try {
                YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                for (String homeName : yaml.getConfigurationSection("homes").getKeys(false)) {

                    String full = "homes." + homeName + ".";

                    String worldName = yaml.getString(full + "world");
                    World world = Bukkit.getWorld(worldName);
                    if (world == null) {
                        logger.warning(ChatColor.RED + "World " + worldName + " does not exist");
                        brokenHomes.add(homeName);
                        continue;
                    }

                    double x = yaml.getDouble(full + "x");
                    double y = yaml.getDouble(full + "y");
                    double z = yaml.getDouble(full + "z");
                    float yaw = Utils.getFloat(yaml, full + "yaw");
                    float pitch = Utils.getFloat(yaml, full + "pitch");

                    Location loc = new Location(world, x, y, z, yaw, pitch);
                    Home converted = new Home(homeName, loc);
                    convertedHomes.add(converted);
                }
            } catch (Exception ex) {
                logger.warning(ChatColor.RED + "Something went wrong while converting Essentials user data " + fileName + ". Error:");
                ex.printStackTrace();
            }


            for (Home convertedHome : convertedHomes) {
                user.createHome(convertedHome);
            }
            try {
                user.save();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //List<Warp> convertedWarps2 = new ArrayList<>(convertedHomes);

        /*for (Warp converted : convertedWarps2) {
            if (!warpManager.registerWarp(converted)) {
                brokenHomes.add(converted.getName());
                convertedHomes.remove(converted);
            }
        }*/

        /*if (brokenHomes.size() == essUsers.length) {
            logger.log(Level.WARNING, ChatColor.RED + "Everything went wrong. Sorry. Your Essentials warps files are broken or you have already converted them.");
            return FinalState.UNSUCCESSFUL;
        }*/

        FinalState state = FinalState.COMPLETED;

        /*if (brokenHomes.isEmpty()) {
            logger.info(ChatColor.GREEN + "All Essentials warps has been converted. Congratulations. Never use it again.");
        } else {
            logger.warning(ChatColor.RED + "Some Essentials warps couldn't be converted. Here is the list of them:");
            brokenHomes.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp));
            state = FinalState.ALMOST;
        }*/

        //logger.info(ChatColor.GREEN + "Successfully converted and registered warps (" + convertedHomes.size() + "):");
        //convertedHomes.forEach(warp -> logger.info(ChatColor.DARK_AQUA + "- " + warp.getName()));
        return state;
    }

    @Override
    public String getName() {
        return "EssentialsHomeConverter";
    }

    @Override
    public String getAuthor() {
        return "bilektugrul";
    }

}
