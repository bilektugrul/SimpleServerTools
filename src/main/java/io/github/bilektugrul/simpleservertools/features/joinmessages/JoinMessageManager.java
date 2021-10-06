package io.github.bilektugrul.simpleservertools.features.joinmessages;

import io.github.bilektugrul.simpleservertools.SST;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class JoinMessageManager {

    private FileConfiguration msgFile;
    private final SST plugin;
    private final List<JoinMessage> joinMessageList = new ArrayList<>();

    public JoinMessageManager(SST plugin) {
        this.plugin = plugin;
        reload();
    }

    public void load() {
        try {
            joinMessageList.clear();
            if (msgFile != null) {
                String s1 = "join-message.per-group.groups";
                String s2 = "join-message.per-permission.permissions";
                String s3 = "join-message.everyone";
                if (msgFile.getBoolean("join-message.per-group.enabled")) {
                    for (String group : msgFile.getConfigurationSection(s1).getKeys(false)) {
                        joinMessageList.add(new JoinMessage(msgFile.getString(s1 + "." + group), group, JoinMessageType.GROUP));
                    }
                }
                if (msgFile.getBoolean("join-message.per-permission.enabled")) {
                    for (String perm : msgFile.getConfigurationSection(s2).getKeys(false)) {
                        joinMessageList.add(new JoinMessage(msgFile.getString(s2 + "." + perm), JoinMessageType.PERMISSION, "sst.joinmessages." + perm));
                    }
                }
                if (msgFile.getBoolean(s3 + ".enabled")) {
                    joinMessageList.add(new JoinMessage(msgFile.getString(s3 + ".message"), JoinMessageType.EVERYONE));
                }
            } else {
                plugin.getLogger().warning("Join messages file couldn't found.");
            }
        } catch (NullPointerException exception) {
            plugin.getLogger().warning("joinmessages.yml is broken. Please re-create it. Stacktrace:");
            exception.printStackTrace();
            plugin.getLogger().warning("joinmessages.yml is broken. Please re-create/correct it.");
        }
    }

    public void reload() {
        this.msgFile = ConfigUtils.getConfig(plugin, "joinmessages");
        load();
    }

    public List<JoinMessage> getList() {
        return new ArrayList<>(joinMessageList);
    }

}