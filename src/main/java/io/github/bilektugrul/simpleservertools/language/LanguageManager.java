package io.github.bilektugrul.simpleservertools.language;

import io.github.bilektugrul.simpleservertools.SST;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.*;
import java.util.Locale;

public class LanguageManager {

    private FileConfiguration language;
    private String languageString;

    private final SST plugin;

    public LanguageManager(SST plugin) {
        this.plugin = plugin;
        loadLanguage();
    }

    public void loadLanguage() {
        languageString = plugin.getConfig().getString("language").toLowerCase(Locale.ROOT);
        String s = File.separator;
        language = ConfigUtils.getConfig(plugin, "language" + s + "messages_" + languageString);
    }

    public FileConfiguration getLanguage() {
        return language;
    }

    public String getLanguageString() {
        return languageString;
    }

    public String getString(String string) {
        return language.getString(string);
    }

    public boolean getBoolean(String string) {
        return getBoolean(string, false);
    }

    public boolean getBoolean(String string, boolean def) {
        return language.getBoolean(string, def);
    }

}
