package io.github.bilektugrul.simpleservertools.listeners;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import io.github.bilektugrul.simpleservertools.features.joinmessage.JoinMessage;
import io.github.bilektugrul.simpleservertools.features.joinmessage.JoinMessageManager;
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
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final SimpleServerTools plugin;
    private final UserManager userManager;
    private final SpawnManager spawnManager;
    private final WarpManager warpManager;
    private final JoinMessageManager joinMessageManager;
    private final VaultManager vaultManager;
    private final VanishManager vanishManager;
    private final TPAManager tpaManager;

    public PlayerListener(SimpleServerTools plugin) {
        this.plugin = plugin;
        this.userManager = plugin.getUserManager();
        this.spawnManager = plugin.getSpawnManager();
        this.warpManager = plugin.getWarpManager();
        this.joinMessageManager = plugin.getJoinMessageManager();
        this.vaultManager = plugin.getVaultManager();
        this.vanishManager = plugin.getVanishManager();
        this.tpaManager = plugin.getTPAManager();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        ArrayList<JoinMessage> msgList = joinMessageManager.getList();

        if (!msgList.isEmpty()) {
            for (JoinMessage msg : msgList) {
                JoinMessage.JoinMessageType type = msg.getType();
                String content = msg.getContent();
                if (type == JoinMessage.JoinMessageType.EVERYONE) {
                    player.sendMessage(Utils.replacePlaceholders(content, player, false));
                } else if (plugin.isPermManagerReady() && type == JoinMessage.JoinMessageType.GROUP) {
                    if ((Arrays.stream(vaultManager.getPermissionProvider().getPlayerGroups(player)).anyMatch(msg.getGroup()::equalsIgnoreCase))) {
                        player.sendMessage(Utils.replacePlaceholders(content, player, false));
                    }
                } else if (type == JoinMessage.JoinMessageType.PERMISSION && player.hasPermission(msg.getPermission())) {
                    player.sendMessage(Utils.replacePlaceholders(content, player, false));
                }
            }
        }

        if (spawnManager.getSpawnFile().getBoolean("spawn.teleport-on-join")) {
            PaperLib.teleportAsync(player, spawnManager.getSpawn().getLocation(), PlayerTeleportEvent.TeleportCause.PLUGIN);
        }

        UUID uuid = player.getUniqueId();
        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!vanishManager.isVanished(uuid)) {
                if (player.hasPlayedBefore())
                    e.setJoinMessage(Utils.getString("join-quit-messages.join-message", player));
                else
                    e.setJoinMessage(Utils.getString("join-quit-messages.first-join-message", player));

            } else {
                e.setJoinMessage("");
            }
        }

        if (!player.hasPermission("sst.staff")) {
            for (UUID vanished : vanishManager.getOnlineVanishedPlayers()) {
                player.hidePlayer(Bukkit.getPlayer(vanished));
            }

        } else if (vanishManager.isVanished(uuid)) vanishManager.hidePlayer(player, true);

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {

        UUID uuid = e.getPlayer().getUniqueId();

        if (Utils.getBoolean("join-quit-messages.enabled", false)) {
            if (!vanishManager.isVanished(uuid)) e.setQuitMessage(Utils.getString("join-quit-messages.quit-message", e.getPlayer()));
        }

        if (vanishManager.isVanished(uuid)) vanishManager.getOnlineVanishedPlayers().remove(uuid);

    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        User attackerUser = userManager.getUser(e.getDamager().getUniqueId());
        if (attackerUser.isGod()) {
            e.setCancelled(true);
        } else if (e.getEntity() instanceof Player) {
            Player victim = (Player) e.getEntity();
            User user = userManager.getUser(victim.getUniqueId());
            if (userManager.isTeleporting(user)) {
                User.State state = user.getState();
                e.setCancelled(getCancelState(user, state));
            }
        } else if (e.getDamager() instanceof Player) {
            User damager = userManager.getUser(e.getDamager().getUniqueId());
            e.setCancelled(getCancelState(damager, damager.getState()));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            User user = userManager.getUser(e.getEntity().getUniqueId());
            if (user.isGod()) {
                e.setCancelled(true);
            }
        }
    }

    public boolean getCancelState(User user, User.State state) {
        TeleportSettings settings;
        if (state == User.State.TELEPORTING) {
            settings = warpManager.getSettings();
        } else if (state == User.State.TELEPORTING_SPAWN) {
            settings = spawnManager.getSettings();
        } else if (state == User.State.TELEPORTING_PLAYER) {
            settings = tpaManager.getSettings();
        } else {
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        if (Utils.getBoolean("auto-respawn.enabled")) {
            if (Utils.getBoolean("auto-respawn.permission-required") && !victim.hasPermission("sst.autorespawn")) {
                return;
            }
            Bukkit.getScheduler().runTask(plugin, () -> victim.spigot().respawn());
        }
        if (spawnManager.getSpawnFile().getBoolean("spawn.teleport-when-die")) {
            spawnManager.teleport(victim, true);
        }
    }

}
