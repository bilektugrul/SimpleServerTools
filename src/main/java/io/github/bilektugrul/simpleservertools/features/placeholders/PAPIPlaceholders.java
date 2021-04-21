package io.github.bilektugrul.simpleservertools.features.placeholders;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIPlaceholders extends PlaceholderExpansion {

    private final SST plugin;
    private final CustomPlaceholderManager placeholderManager;
    private final VanishManager vanishManager;

    public PAPIPlaceholders(SST plugin) {
        this.plugin = plugin;
        this.placeholderManager = plugin.getPlaceholderManager();
        this.vanishManager = plugin.getVanishManager();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "sst";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {

        boolean playerRequired = (identifier.equals("safeonline"));

        if (playerRequired) {
            if (player == null) return "";
        }

        if (identifier.equals("safeonline")) {
            if (!player.hasPermission("sst.staff")) {
                return String.valueOf(Bukkit.getOnlinePlayers().size() - vanishManager.getOnlineVanishedPlayers().size());
            } else {
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            }
        }

        if (identifier.equals("vanished")){
            return String.valueOf(vanishManager.getOnlineVanishedPlayers().size());
        } else if (identifier.contains("custom")) {
            String name = identifier.substring(identifier.indexOf("custom_") + 7);
            return placeholderManager.getPlaceholder(name).getValue();
        }
        return null;
    }

}