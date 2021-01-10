package io.github.bilektugrul.simplevanish.placeholders;

import io.github.bilektugrul.simplevanish.SimpleVanish;
import io.github.bilektugrul.simplevanish.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIPlaceholders extends PlaceholderExpansion {

    private final String permission = Utils.getString("vanish-command-permission");

    private final SimpleVanish plugin;

    public PAPIPlaceholders(SimpleVanish plugin){
        this.plugin = plugin;
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
        return "simplevanish";
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

        if (identifier.equals("safeonline")){
            if (!player.hasPermission(permission)) {
                return String.valueOf(Bukkit.getOnlinePlayers().size() - plugin.getOnlineVanishedPlayers().size());
            } else {
                return String.valueOf(Bukkit.getOnlinePlayers().size());
            }
        }

        if (identifier.equals("vanished")){
            return String.valueOf(plugin.getOnlineVanishedPlayers().size());
        }

        return null;
    }

}