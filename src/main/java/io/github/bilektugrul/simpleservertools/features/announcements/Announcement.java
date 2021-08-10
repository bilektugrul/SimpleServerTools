package io.github.bilektugrul.simpleservertools.features.announcements;

public record Announcement(AnnouncementType type, String permission, String group, String content) {

    public Announcement(AnnouncementType type, String group, String content) {
        this(type, null, group, content);
    }

    public Announcement(String content, String permission, AnnouncementType type) {
        this(type, permission, null, content);
    }

    public Announcement(String content) {
        this(AnnouncementType.NONE, null, null, content);
    }

}
