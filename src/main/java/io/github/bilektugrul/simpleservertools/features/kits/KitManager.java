package io.github.bilektugrul.simpleservertools.features.kits;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.compat.XMaterial;
import me.despical.commons.configuration.ConfigUtils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class KitManager {

    private final SST plugin;
    private final Economy economy;
    private final Set<Kit> kits = new HashSet<>();

    private FileConfiguration kitsFile;

    public KitManager(SST plugin) {
        this.plugin = plugin;
        this.economy = plugin.getVaultManager().getEconomyProvider();
        reload();
    }

    public boolean canGiveKit(CommandSender sender, Kit kit, Player player, User user) {
        if (!sender.hasPermission(kit.getPermission())) {
            sender.sendMessage(Utils.getMessage("kits.no-permission", sender));
            return false;
        }

        boolean isSame = sender.equals(player);
        if (!isSame && !sender.hasPermission("sst.kits.others")) {
            sender.sendMessage(Utils.getMessage("kits.no-permission-others", sender));
            return false;
        }

        CommandSender toSend = isSame ? player : sender;
        if (!user.canTakeKit(player, kit)) {
            String message;

            if (isSame) {
                message = Utils.getMessage("kits.in-cooldown", player);
            } else {
                message = Utils.getMessage("kits.in-cooldown-other", sender)
                        .replace("%other%", player.getName());
            }

            message = message
                    .replace("%cooldown%", user.getFormattedRemainingCooldown(kit))
                    .replace("%fulldate%", user.getFormattedNextKitDate(kit));
            toSend.sendMessage(message);
            return false;
        }

        if (!economy.has(player, kit.price())) {
            String message;

            if (isSame) {
                message = Utils.getMessage("kits.not-enough-money", player);
            } else {
                message = Utils.getMessage("kits.not-enough-money-others", sender);
            }

            message = message
                    .replace("%kit%", kit.id())
                    .replace("%money%", String.valueOf(kit.price()))
                    .replace("%kituser%", player.getName());
            toSend.sendMessage(message);
            return false;
        }
        return true;
    }

    public void giveKit(CommandSender sender, Kit kit, Player player, User user) {
        if (canGiveKit(sender, kit, player, user)) {
            economy.withdrawPlayer(player, kit.price());
            giveItems(kit.items(), player);
            user.putKitCooldown(player, kit);
        }
    }

    public void giveItems(List<Kit.KitItem> items, Player player) {
        Inventory inventory = player.getInventory();
        World world = player.getWorld();
        Location location = player.getLocation();

        for (Kit.KitItem item : items) {
            ItemStack realItem = item.item();
            if (Utils.hasSpace(inventory, realItem)) {
                inventory.addItem(realItem);
            } else {
                world.dropItem(location, realItem);
            }
        }
    }

    public void reload() {
        kitsFile = ConfigUtils.getConfig(plugin, "kits");
        load();
    }

    public void load() {
        kits.clear();
        for (String key : kitsFile.getConfigurationSection("kits").getKeys(false)) {
            loadKit(kitsFile.getConfigurationSection("kits." + key));
        }
    }

    public void loadKit(ConfigurationSection section) {
        String id = section.getName();
        long cooldown = section.getLong("cooldown", 0);
        long price = section.getLong("price", 0);
        List<Kit.KitItem> items = new ArrayList<>();
        for (String key : section.getConfigurationSection("items").getKeys(false)) {
            String path = "items." + key;

            Kit.KitItem item;
            if (section.isConfigurationSection(path)) {
                item = loadItem(section.getConfigurationSection(path));
            } else if (section.isItemStack(path)) {
                item = new Kit.KitItem(key, section.getItemStack(path));
            } else {
                continue;
            }

            items.add(item);
        }
        Kit kit = new Kit(id, items, cooldown, price);
        addKit(kit);
    }

    public Kit.KitItem loadItem(ConfigurationSection section) {
        ItemStack item = XMaterial.matchXMaterial(section.getString("material")).orElseThrow().parseItem();
        ItemMeta meta = item.getItemMeta();

        if (section.contains("name")) {
            meta.setDisplayName(Utils.colorize(section.getString("name")));
        }

        if (section.contains("lore")) {
            List<String> lore = new ArrayList<>();
            for (String value : section.getStringList("lore")) {
                lore.add(Utils.colorize(value));
            }
            meta.setLore(lore);
        }
        item.setItemMeta(meta);

        if (section.contains("enchants")) {
            Map<Enchantment, Integer> enchants = new HashMap<>();

            for (String enchant : section.getStringList("enchants")) {
                String[] enchArray = enchant.split(" ");
                Enchantment value = Enchantment.getByName(enchArray[0]);
                int level = Integer.parseInt(enchArray[1]);
                enchants.put(value, level);
            }

            item.addUnsafeEnchantments(enchants);
        }
        return new Kit.KitItem(section.getName(), item);
    }

    public void addKit(Kit kit) {
        if (kit.id().equalsIgnoreCase("list")) {
            return;
        }

        kits.add(kit);
    }

    public boolean isPresent(String id) {
        for (Kit kit : kits) {
            if (kit.id().equalsIgnoreCase(id)) {
                return true;
            }
        }

        return false;
    }

    public void saveKit(Kit kit) {
        String path = "kits." + kit.id() + ".";
        kitsFile.set(path + "cooldown", kit.cooldown());
        kitsFile.set(path + "price", kit.price());
        for (Kit.KitItem kitItem : kit.items()) {
            saveKitItem(kit, kitItem);
        }
    }

    public void saveKitItem(Kit kit, Kit.KitItem kitItem) {
        String path = "kits." + kit.id() + ".items." + kitItem.id();
        kitsFile.set(path, kitItem.item());
    }

    public void saveKits() {
        kitsFile.set("kits", null);
        for (Kit kit : kits) {
            saveKit(kit);
        }

        ConfigUtils.saveConfig(plugin, kitsFile, "kits");
    }

    public String readableKitList(CommandSender sender) {
        boolean isAdmin = sender.hasPermission("sst.admin");
        if (!kits.isEmpty()) {
            List<String> kits = this.kits.stream()
                    .filter(kit -> isAdmin || sender.hasPermission(kit.getPermission()))
                    .map(Kit::id)
                    .collect(Collectors.toList());
            return String.join(", ", kits);
        } else {
            return Utils.getMessage("kits.no-kits", null);
        }
    }

    public Kit getKit(String id) {
        for (Kit kit : kits) {
            if (kit.id().equalsIgnoreCase(id)) {
                return kit;
            }
        }

        return null;
    }

    public Set<Kit> getKits() {
        return new HashSet<>(kits);
    }

}