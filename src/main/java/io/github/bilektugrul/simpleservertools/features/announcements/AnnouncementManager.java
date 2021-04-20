package io.github.bilektugrul.simpleservertools.features.announcements;

import io.github.bilektugrul.simpleservertools.SimpleServerTools;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementManager {

    private final SimpleServerTools plugin;
    private AsyncAnnouncementTask announcementTask;
    private FileConfiguration announcementsFile;
    private final List<Announcement> announcements = new ArrayList<>();

    public AnnouncementManager(SimpleServerTools plugin) {
        this.plugin = plugin;
        reload();
    }

    public void load() {
        announcements.clear();
        String start = "announcements.announcements";
        for (String key : announcementsFile.getConfigurationSection(start).getKeys(false)) {
            String full = start + "." + key + ".";
            String type = announcementsFile.getString(full + "filter.type");
            if (type.equalsIgnoreCase("GROUP")) {
                String group = announcementsFile.getString(full + "filter.group");
                String content = announcementsFile.getString(full + "content");
                announcements.add(new Announcement(AnnouncementType.GROUP, group, content));
            } else if (type.equalsIgnoreCase("PERMISSION")) {
                String permission = announcementsFile.getString(full + "filter.permission");
                String content = announcementsFile.getString(full + "content");
                announcements.add(new Announcement(content, permission, AnnouncementType.PERMISSION));
            } else if (type.equalsIgnoreCase("NONE")) {
                String content = announcementsFile.getString(full + "content");
                announcements.add(new Announcement(content));
            }
        }
    }

    public void reload() {
        announcementsFile = ConfigUtils.getConfig(plugin, "announcements");
        load();
        if (announcementTask != null && announcementTask.hasStarted) {
            announcementTask.cancel();
            announcementTask = null;
        }
        check();
    }

    public void check() {
        boolean enabled = announcementsFile.getBoolean("announcements.enabled");
        if (enabled) {
            announcementTask = new AsyncAnnouncementTask(this, plugin, matchMode());
            start();
        } else if (announcementTask != null && announcementTask.hasStarted) {
            announcementTask.cancel();
            announcementTask = null;
        }
    }

    public AnnouncementMode matchMode() {
        return (announcementsFile.getBoolean("announcements.random")) ? AnnouncementMode.RANDOM : AnnouncementMode.ORDERED;
    }

    public void start() {
        announcementTask.start();
    }

    public List<Announcement> getAnnouncements() {
        return announcements;
    }

    public FileConfiguration getAnnouncementsFile() {
        return announcementsFile;
    }

    public AsyncAnnouncementTask getAnnouncementTask() {
        return announcementTask;
    }
}
