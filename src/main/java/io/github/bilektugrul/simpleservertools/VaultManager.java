package io.github.bilektugrul.simpleservertools;

import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {

    private final SimpleServerTools plugin;
    private Permission permissionProvider;

    public VaultManager(SimpleServerTools plugin) {
        this.plugin = plugin;
    }

    public void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        permissionProvider = rsp.getProvider();
    }

    public Permission getPermissionProvider() {
        return permissionProvider;
    }

}
