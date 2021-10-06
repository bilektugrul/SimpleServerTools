package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SST;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {

    private final SST plugin;
    private Permission permissionProvider;

    public VaultManager(SST plugin) {
        this.plugin = plugin;
        setupPermissions();
    }

    public void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        permissionProvider = rsp.getProvider();
    }

    public Permission getPermissionProvider() {
        return permissionProvider;
    }

}