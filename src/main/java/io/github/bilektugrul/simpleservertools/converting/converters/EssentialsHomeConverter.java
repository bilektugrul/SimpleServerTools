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
import java.util.Arrays;
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
        long start = System.currentTimeMillis();
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

        List<String> detailedLog = new ArrayList<>();

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            int count = 0;
            for (File file : essUsers) {
                List<Home> convertedHomes = new ArrayList<>();

                String fileName = file.getName();
                String uuid = fileName.replace(".yml", "");
                User user = null;

                try {
                    YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
                    if (yaml.isConfigurationSection("homes")) {
                        for (String homeName : yaml.getConfigurationSection("homes").getKeys(false)) {

                            String full = "homes." + homeName + ".";

                            String worldName = yaml.getString(full + "world");
                            assert worldName != null;
                            World world = Bukkit.getWorld(worldName);
                            if (world == null) {
                                detailedLog.add(ChatColor.RED + "World '" + worldName + "' does not exist. (Warning from: home '" + homeName + "' of '" + uuid + "')");
                                continue;
                            }

                            user = userManager.loadUser(UUID.fromString(uuid), false);
                            count++;

                            double x = yaml.getDouble(full + "x");
                            double y = yaml.getDouble(full + "y");
                            double z = yaml.getDouble(full + "z");
                            float yaw = Utils.getFloat(yaml, full + "yaw");
                            float pitch = Utils.getFloat(yaml, full + "pitch");

                            Location loc = new Location(world, x, y, z, yaw, pitch);
                            Home converted = new Home(homeName, loc);
                            convertedHomes.add(converted);
                        }
                    }
                } catch (Exception ex) {
                    detailedLog.add(ChatColor.RED + "Something went wrong while converting Essentials user data " + fileName + ". Error:" + Arrays.toString(ex.getStackTrace()));
                }

                if (user != null) {
                    for (Home convertedHome : convertedHomes) {
                        if (!user.createHome(convertedHome)) {
                            detailedLog.add(uuid + " already has a home with name " + convertedHome.getName());
                        }
                    }

                    try {
                        user.save();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            detailedLog.add(System.lineSeparator() + ChatColor.GREEN + (count + " of user home data converted in " + (System.currentTimeMillis() - start) + "ms"));
            logger.info(Utils.listToString(detailedLog));
        });

        return FinalState.STILL_RUNNING;
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
