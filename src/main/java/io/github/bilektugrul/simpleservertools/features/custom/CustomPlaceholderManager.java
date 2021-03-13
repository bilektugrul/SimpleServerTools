package io.github.bilektugrul.simpleservertools.features.custom;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class CustomPlaceholderManager {

    private final SimpleServerTools plugin;
    private final Set<CustomPlaceholder> placeholderList = new HashSet<>();

    public CustomPlaceholderManager(SimpleServerTools plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        placeholderList.clear();
        FileConfiguration config = plugin.getConfig();
        for (String key : config.getConfigurationSection("custom-placeholders").getKeys(false)) {
            CustomPlaceholder placeholder = new CustomPlaceholder(key, config.getString("custom-placeholders." + key));
            placeholderList.add(placeholder);
        }
    }

    public String replacePlaceholders(String in) {
        for (CustomPlaceholder placeholder : placeholderList) {
            in = in.replace("%" + placeholder.getName() + "%", placeholder.getValue());
        }
        return in;
    }

    public Set<CustomPlaceholder> getPlaceholderList() {
        return placeholderList;
    }

    public CustomPlaceholder getPlaceholder(String name) {
        for (CustomPlaceholder placeholder : placeholderList) {
            if (placeholder.getName().equalsIgnoreCase(name)) {
                return placeholder;
            }
        }
        return null;
    }

}