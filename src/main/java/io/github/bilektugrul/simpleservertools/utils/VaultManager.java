package io.github.bilektugrul.simpleservertools.utils;

import io.github.bilektugrul.simpleservertools.SST;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultManager {

    private final SST plugin;
    private Permission permissionProvider;
    private Economy economyProvider;

    public VaultManager(SST plugin) {
        this.plugin = plugin;
        setupPermissions();
        setupEconomy();
    }

    public void setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
        permissionProvider = rsp.getProvider();
    }

    public void setupEconomy() {
        RegisteredServiceProvider<Economy> economy = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        this.economyProvider = economy.getProvider();
    }

    public Permission getPermissionProvider() {
        return permissionProvider;
    }

    public Economy getEconomyProvider() {
        return economyProvider;
    }

}