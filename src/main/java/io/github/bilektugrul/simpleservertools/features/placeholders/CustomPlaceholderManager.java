package io.github.bilektugrul.simpleservertools.features.placeholders;

import io.github.bilektugrul.simpleservertools.SST;
import me.despical.commons.util.Strings;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CustomPlaceholderManager {

    private final SST plugin;
    private final Set<CustomPlaceholder> placeholderList = new HashSet<>();

    public CustomPlaceholderManager(SST plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        placeholderList.clear();
        FileConfiguration config = plugin.getConfig();
        for (String key : config.getConfigurationSection("custom-placeholders").getKeys(false)) {
            CustomPlaceholder placeholder = new CustomPlaceholder(key, colorize(config.getString("custom-placeholders." + key)));
            placeholderList.add(placeholder);
        }
    }

    public String replacePlaceholders(String in) {
        for (CustomPlaceholder placeholder : placeholderList) {
            in = in.replace("%" + placeholder.name() + "%", placeholder.value());
        }
        return in;
    }

    public String colorize(String string) {
        return Strings.format(string);
    }

    public Set<CustomPlaceholder> getPlaceholderList() {
        return new HashSet<>(placeholderList);
    }

    public CustomPlaceholder getPlaceholder(String name) {
        for (CustomPlaceholder placeholder : placeholderList) {
            if (placeholder.name().equalsIgnoreCase(name)) {
                return placeholder;
            }
        }
        return null;
    }

}