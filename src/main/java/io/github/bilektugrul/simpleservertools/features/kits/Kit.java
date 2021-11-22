package io.github.bilektugrul.simpleservertools.features.kits;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Kit(String id, List<KitItem> items, long cooldown, long price) {

    public String getPermission() {
        return "sst.kits." + id;
    }

    public record KitItem(String id, ItemStack item) {}

}