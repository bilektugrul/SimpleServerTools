package io.github.bilektugrul.simpleservertools.features.placeholders;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PAPIPlaceholders extends PlaceholderExpansion {

    private final SST plugin;
    private final CustomPlaceholderManager placeholderManager;
    private final VanishManager vanishManager;
    private final UserManager userManager;

    public PAPIPlaceholders(SST plugin) {
        this.plugin = plugin;
        this.placeholderManager = plugin.getPlaceholderManager();
        this.vanishManager = plugin.getVanishManager();
        this.userManager = plugin.getUserManager();
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

        boolean playerRequired = identifier.equals("safeonline");
        boolean userRequired = identifier.contains("status");

        if (playerRequired) {
            if (player == null) return "";
        }

        if (identifier.equals("safeonline")) {
            int size = Bukkit.getOnlinePlayers().size();
            if (!player.hasPermission("sst.staff")) {
                return String.valueOf(size - vanishManager.getOnlineVanishedPlayers().size());
            } else {
                return String.valueOf(size);
            }
        }

        if (identifier.equals("vanished")) {
            return String.valueOf(vanishManager.getOnlineVanishedPlayers().size());

        } else if (identifier.contains("custom")) {
            String name = identifier.substring(identifier.indexOf("custom_") + 7);
            return placeholderManager.getPlaceholder(name).getValue();

        } else if (userRequired) {
            User user = userManager.getUser(player);
            if (identifier.endsWith("msg")) {
                return Utils.getMessage("user-statuses.msg." + user.isAcceptingMsg());
            } else if (identifier.endsWith("tpa")){
                return Utils.getMessage("user-statuses.tpa." + user.isAcceptingTPA());
            } else if (identifier.endsWith("afk")) {
                return Utils.getMessage("user-statuses.afk." + user.isAfk());
            }
        }

        return null;
    }

}