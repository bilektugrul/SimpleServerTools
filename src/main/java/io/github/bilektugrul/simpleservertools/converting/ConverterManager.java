package io.github.bilektugrul.simpleservertools.converting;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.converting.converters.CMIWarpConverter;
import io.github.bilektugrul.simpleservertools.converting.converters.EssentialsHomeConverter;
import io.github.bilektugrul.simpleservertools.converting.converters.EssentialsWarpConverter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConverterManager {

    private final HashMap<Converter, List<String>> converters = new HashMap<>();

    public ConverterManager(SST plugin) {
        registerConverter(new EssentialsWarpConverter(plugin), "esswarps", "esswarp", "ewarp", "ewarps", "eswarp", "eswarps");
        registerConverter(new CMIWarpConverter(plugin), "cmiwarps", "cmiwarp", "cwarp", "cwarps", "cmwarp", "cmwarps");
        registerConverter(new EssentialsHomeConverter(plugin), "esshomes", "ehomes", "esshome", "ehome", "eshomes", "eshome");
    }

    public void registerConverter(Converter converter, String... aliases) {
        converters.put(converter, Arrays.asList(aliases));
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
