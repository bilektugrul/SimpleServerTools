package io.github.bilektugrul.simpleservertools.listeners;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessage;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageManager;
import io.github.bilektugrul.simpleservertools.features.joinmessages.JoinMessageType;
import io.github.bilektugrul.simpleservertools.features.maintenance.MaintenanceManager;
import io.github.bilektugrul.simpleservertools.features.spawn.SpawnManager;
import io.github.bilektugrul.simpleservertools.features.tpa.TPAManager;
import io.github.bilektugrul.simpleservertools.features.vanish.VanishManager;
import io.github.bilektugrul.simpleservertools.features.warps.WarpManager;
import io.github.bilektugrul.simpleservertools.stuff.CancelMode;
import io.github.bilektugrul.simpleservertools.stuff.teleporting.TeleportSettings;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.users.UserManager;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import io.github.bilektugrul.simpleservertools.utils.VaultManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final SST plugin;
    private final UserManager userManager;
    private final SpawnManager spawnManager;
    private final WarpManager warpManager;
    private final JoinMessageManager joinMessageManager;
    private final VaultManager vaultManager;
    private final VanishManager vanishManager;
    private final TPAManager tpaManager;
    private final MaintenanceManager maintenanceManager;

    public PlayerListener(SST plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.spawnManager = plugin.getSpawnManager();
        this.warpManager = plugin.getWarpManager();
        this.joinMessageManager = plugin.getJoinMessageManager();
        this.vaultManager = plugin.getVaultManager();
        this.vanishManager = plugin.getVanishManager();
        this.tpaManager = plugin.getTPAManager();
        this.maintenanceManager = plugin.getMaintenanceManager();
    }

    @EventHandler
    public void onConnect(PlayerLoginEvent e) {
        Player p = e.getPlayer();
        if (maintenanceManager.inMaintenance && !p.hasPermission("sst.maintenance.join")) {
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.getString(maintenanceManager.getMaintenanceFile(), "maintenance.in-maintenance-message", p)
                    .replace("%reason%", Utils.replacePlaceholders(maintenanceManager.getReason(), p, true)));
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        userManager.loadUser(player);

        ArrayList<JoinMessage> msgList = joinMessageManager.getList();

        for (JoinMessage msg : msgList) {
            JoinMessageType type = msg.getType();
            String content = msg.getContent();
            if (type == JoinMessageType.EVERYONE) {
                player.sendMessage(Utils.replacePlaceholders(content, player, true));
            } else if (plugin.isPermManagerReady() && type == JoinMessageType.GROUP) {
                if (Arrays.stream(vaultManager.getPermissionProvider().getPlayerGroups(player)).anyMatch(msg.getGroup()::equalsIgnoreCase)) {
                    player.sendMessage(Utils.replacePlaceholders(content, player, true));
                }
            } else if (type == JoinMessageType.PERMISSION && player.hasPermission(msg.getPermission())) {
                player.sendMessage(Utils.replacePlaceholders(content, player, true));
            }
        }

        if (spawnManager.getSpawnFile().getBoolean("spawn.teleport-on-join")) {
            PaperLib.teleportAsync(player, spawnManager.getSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!vanishManager.isVanished(uuid)) {
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
        } else if (vanishManager.isVanished(uuid)) {
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

        if (attackerEntity instanceof Player) {
            User attackerUser = userManager.getUser((Player) attackerEntity);
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
        Entity victim = e.getEntity();
        if (victim instanceof Player) {
            Player victimPlayer = (Player) victim;
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

    @EventHandler(priority = EventPriority.LOWEST)
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
        switch (user.getState()) {
            case TELEPORTING:
                return warpManager.getSettings();
            case TELEPORTING_SPAWN:
                return spawnManager.getSettings();
            case TELEPORTING_PLAYER:
                return tpaManager.getSettings();
            default:
                return null;
        }
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
