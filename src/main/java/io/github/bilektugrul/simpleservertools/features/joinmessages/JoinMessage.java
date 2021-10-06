package io.github.bilektugrul.simpleservertools.features.joinmessages;

public record JoinMessage(String content, JoinMessageType type, String group, String permission) {

    public JoinMessage(String content, String group, JoinMessageType type) {
        this(content, type, group, null);
    }

    public JoinMessage(String content, JoinMessageType type, String permission) {
        this(content, type, null, permission);
    }

    public JoinMessage(String content, JoinMessageType type) {
        this(content, type, null, null);
    }

}