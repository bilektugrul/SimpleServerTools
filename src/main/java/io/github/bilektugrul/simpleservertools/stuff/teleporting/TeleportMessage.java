package io.github.bilektugrul.simpleservertools.stuff.teleporting;

import io.github.bilektugrul.simpleservertools.users.User;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.entity.Player;

public class TeleportMessage {

    private final MessageMode messageMode;
    private final User user;

    private String teleportingChat, teleportingActionBar, teleportedChat, teleportedActionBar;
    private String teleportingTitle, teleportingSub, teleportedTitle, teleportedSub;

    public TeleportMessage(Player player, User user, String mode, MessageMode messageMode) {
        this.messageMode = messageMode;
        this.user = user;

        if (messageMode == MessageMode.TELEPORTING) {
            this.teleportingChat = Utils.getMessage(mode + ".teleporting.chat", player);
            this.teleportingActionBar = Utils.getMessage(mode + ".teleporting.actionbar", player);
            this.teleportingTitle = Utils.getMessage(mode + ".teleporting.title.title", player);
            this.teleportingSub = Utils.getMessage(mode + ".teleporting.title.subtitle", player);
        } else {
            this.teleportedChat = Utils.getMessage(mode + ".teleported.chat", player);
            this.teleportedActionBar = Utils.getMessage(mode + ".teleported.actionbar", player);
            this.teleportedTitle = Utils.getMessage(mode + ".teleported.title.title", player);
            this.teleportedSub = Utils.getMessage(mode + ".teleported.title.subtitle", player);
        }

    }

    public String getTeleportingChat() {
        return teleportingChat;
    }

    public String getTeleportingActionBar() {
        return teleportingActionBar;
    }

    public String getTeleportedChat() {
        return teleportedChat;
    }

    public String getTeleportedActionBar() {
        return teleportedActionBar;
    }

    public String getTeleportingTitle() {
        return teleportingTitle;
    }

    public String getTeleportingSub() {
        return teleportingSub;
    }

    public String getTeleportedTitle() {
        return teleportedTitle;
    }

    public String getTeleportedSub() {
        return teleportedSub;
    }

    public MessageMode getMessageMode() {
        return messageMode;
    }

}