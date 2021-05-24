package io.github.bilektugrul.simpleservertools.converting;

import io.github.bilektugrul.simpleservertools.SST;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterManager {

    private final HashMap<Converter, List<String>> converters = new HashMap<>();
    private final FileConfiguration convertersFile;
    private final SST plugin;

    public ConverterManager(SST plugin) {
        this.plugin = plugin;
        convertersFile = ConfigUtils.getConfig(plugin, "converters" + File.separator + "converters");
        loadConverters();
    }

    private void loadConverters() {
        converters.clear();
        for (String key : convertersFile.getConfigurationSection("converters").getKeys(false)) {
            try {
                String name = key.replace('-', '.');
                Class<?> converterClass = Class.forName(name);
                Constructor<?> constructor = converterClass.getConstructor(SST.class);
                Object instance = constructor.newInstance(plugin);
                Converter converter = (Converter) instance;
                List<String> aliases = convertersFile.getStringList("converters." + key + ".aliases");
                converters.put(converter, aliases);
            } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public Converter findConverter(String alias) {
        for (Map.Entry<Converter, List<String>> entry : converters.entrySet()) {
            if (entry.getValue().contains(alias)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public HashMap<Converter, List<String>> getConverters() {
        return converters;
    }

}
