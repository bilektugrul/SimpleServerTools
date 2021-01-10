package io.github.bilektugrul.simplevanish;

import io.github.bilektugrul.simplevanish.commands.*;
import io.github.bilektugrul.simplevanish.listeners.*;
import io.github.bilektugrul.simplevanish.placeholders.PAPIPlaceholders;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleVanish extends JavaPlugin {

    private List<UUID> vanishedPlayers = new ArrayList<>();
    private List<UUID> onlineVanishedPlayers = new ArrayList<>();

    public List<UUID> getVanishedPlayers() {
        return vanishedPlayers;
    }
    public List<UUID> getOnlineVanishedPlayers() {
        return onlineVanishedPlayers;
    }


    @Override
    public void onEnable() {
        saveDefaultConfig();
        new PlayerJoinListener(this);
        new PlayerQuitListener(this);
        new VanishCommand(this);
        new PacketListener(this);
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PAPIPlaceholders(this).register();
        }
        getLogger().info("SimpleVanish " + getDescription().getVersion() + " activated!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SimpleVanish " + getDescription().getVersion() + " disabled!");
    }

}
