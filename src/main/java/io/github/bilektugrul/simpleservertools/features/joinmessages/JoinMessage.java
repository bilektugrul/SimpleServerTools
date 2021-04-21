package io.github.bilektugrul.simpleservertools.features.joinmessages;

public class JoinMessage {

    private final String content;
    private final JoinMessageType type;
    private final String group;
    private final String permission;

    public JoinMessage(String content, JoinMessageType type, String group, String permission) {
        this.content = content;
        this.type = type;
        this.group = group;
        this.permission = permission;
    }

    public JoinMessage(String content, String group, JoinMessageType type) {
        this(content, type, group, null);
    }

    public JoinMessage(String content, JoinMessageType type, String permission) {
        this(content, type, null, permission);
    }

    public JoinMessage(String content, JoinMessageType type) {
        this(content, type, null, null);
    }

    public String getContent() {
        return content;
    }

    public JoinMessageType getType() {
        return type;
    }

    public String getGroup() {
        return group;
    }

    public String getPermission() {
        return permission;
    }

}
