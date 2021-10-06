package io.github.bilektugrul.simpleservertools.listeners;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessage;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageType;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.papermc.lib.PaperLib;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerListener extends ListenerAdapter {

    public PlayerListener(SST plugin) {
        super(plugin);
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (maintenanceManager.isFullyClosed()) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, maintenanceManager.getFullyClosedMessage());
        } else if (maintenanceManager.isInMaintenance() && !p.hasPermission("sst.maintenance.join")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.getString(maintenanceManager.getMaintenanceFile(), "maintenance.in-maintenance-message", p)
                    .replace("%reason%", Utils.replacePlaceholders(maintenanceManager.getReason(), p, true)));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        userManager.loadUser(player);

        List<JoinMessage> msgList = joinMessageManager.getList();

        for (JoinMessage msg : msgList) {
            JoinMessageType type = msg.type();
            String content = msg.content();
            if (type == JoinMessageType.EVERYONE) {
                player.sendMessage(Utils.replacePlaceholders(content, player, true));
            } else if (plugin.isPermManagerReady() && type == JoinMessageType.GROUP) {
                Permission permissionProvider = vaultManager.getPermissionProvider();
                if (Arrays.stream(permissionProvider.getPlayerGroups(player)).anyMatch(msg.group()::equalsIgnoreCase)) {
                    player.sendMessage(Utils.replacePlaceholders(content, player, true));
                }
            } else if (type == JoinMessageType.PERMISSION && player.hasPermission(msg.permission())) {
                player.sendMessage(Utils.replacePlaceholders(content, player, true));
            }
        }

        if (spawnManager.getSpawnFile().getBoolean("spawn.teleport-on-join")) {
            PaperLib.teleportAsync(player, spawnManager.getSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        boolean isVanished = vanishManager.isVanished(uuid);

        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!isVanished) {
                if (player.hasPlayedBefore()) {
                    e.setJoinMessage(Utils.getString("join-quit-messages.join-message", player));
                } else {
                    e.setJoinMessage(Utils.getString("join-quit-messages.first-join-message", player));
                }
            } else {
                e.setJoinMessage("");
            }
        }

        if (!player.hasPermission("sst.staff")) {
            for (UUID vanished : vanishManager.getOnlineVanishedPlayers()) {
                player.hidePlayer(Bukkit.getPlayer(vanished));
            }
        } else if (isVanished) {
            vanishManager.hidePlayer(player, true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        User user = userManager.getUser(player);
        user.save();
        userManager.getUserList().remove(user);

        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!vanishManager.isVanished(uuid)) e.setQuitMessage(Utils.getString("join-quit-messages.quit-message", player));
        }

        if (vanishManager.isVanished(uuid)) vanishManager.getOnlineVanishedPlayers().remove(uuid);

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity attackerEntity = e.getDamager();
        Entity victimEntity = e.getEntity();

        boolean isVictimPlayer = victimEntity instanceof Player;

        if (attackerEntity instanceof Player attackerPlayer) {
            User attackerUser = userManager.getUser(attackerPlayer);
            if (!isVictimPlayer) {
                e.setCancelled(attackerUser.isGod() || getCancelState(attackerUser));
            }
            return;
        }

        if (!e.isCancelled() && isVictimPlayer) {
            Player victim = (Player) victimEntity;
            if (victim.isOnline()) { // NPC check
                User victimUser = userManager.getUser(victim);
                if (userManager.isTeleporting(victimUser)) {
                    e.setCancelled(getCancelState(victimUser));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player victimPlayer) {
            if (e.getCause() == EntityDamageEvent.DamageCause.VOID && Utils.getBoolean("falling-into-void.teleport-spawn")) {
                victimPlayer.teleport(spawnManager.getSpawn().getLocation());
                if (Utils.getBoolean("falling-into-void.cancel-damage"))
                    e.setCancelled(true);
                return;
            }
            if (victimPlayer.isOnline()) { // NPC check
                User user = userManager.getUser(victimPlayer);
                e.setCancelled(user.isGod());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        if (spawnManager.getSpawnFile().getBoolean("spawn.teleport-when-die")) {
            spawnManager.teleport(victim, true);
        }
        if (Utils.getBoolean("auto-respawn.enabled")) {
            if (!Utils.getBoolean("auto-respawn.permission-required") || victim.hasPermission("sst.autorespawn")) {
                Bukkit.getScheduler().runTask(plugin, () -> victim.spigot().respawn());
            }
        }
        if (Utils.getBoolean("disable-death-messages")) {
            e.setDeathMessage("");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent e) {
        Player player = e.getPlayer();
        User user = userManager.getUser(player);
        TeleportSettings settings = getCurrentSettings(user);

        if (settings != null && settings.doesBlockCommands()) {

            boolean isStaff = player.hasPermission("sst.staff");
            CancelMode cancelCommandsMode = settings.getCancelCommandsMode();

            if (cancelCommandsMode == CancelMode.EVERYONE || (cancelCommandsMode == CancelMode.EXCEPT_STAFF && !isStaff)) {
                e.setCancelled(true);
            } else {
                e.setCancelled(cancelCommandsMode == CancelMode.STAFF && isStaff);
            }
            if (e.isCancelled()) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> player.closeInventory(), 2); // fix stupid DeluxeMenus error - drives me crazy
                player.sendMessage(Utils.getMessage("command-blocked", player));
            }

        }

    }

    private TeleportSettings getCurrentSettings(User user) {
        return switch (user.getState()) {
            case TELEPORTING -> warpManager.getSettings();
            case TELEPORTING_SPAWN -> spawnManager.getSettings();
            case TELEPORTING_PLAYER -> tpaManager.getSettings();
            case TELEPORTING_HOME -> homeManager.getSettings();
            default -> null;
        };
    }

    private boolean getCancelState(User user) {
        TeleportSettings settings = getCurrentSettings(user);
        if (settings == null) {
            return false;
        }
        final boolean isStaff = user.getPlayer().hasPermission("sst.staff");
        final CancelMode damageCancelMode = settings.getCancelDamageMode();
        if (damageCancelMode == CancelMode.EVERYONE || (damageCancelMode == CancelMode.EXCEPT_STAFF && !isStaff)) {
            return true;
        } else {
            return damageCancelMode == CancelMode.STAFF && isStaff;
        }
    }

}