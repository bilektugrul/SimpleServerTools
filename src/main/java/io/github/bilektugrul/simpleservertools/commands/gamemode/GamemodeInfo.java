package io.github.bilektugrul.simpleservertools.commands.gamemode;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeInfo {

    public Player player;
    public GameMode gameMode;

    public GamemodeInfo(Player player, GameMode gameMode) {
        this.player = player;
        this.gameMode = gameMode;
    }

    public GamemodeInfo() {
        this(null, null);
    }

    public Player getPlayer() {
        return player;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isCompleted() {
        return player != null && gameMode != null;
    }

    public void apply(CommandSender from) {
        if (isCompleted()) {
            player.setGameMode(gameMode);
            sendMessage(from);
        } else {
            from.sendMessage(Utils.getMessage("messages.gamemode.wrong-arguments", from));
        }
    }

    public void sendMessage(CommandSender from) {
        player.sendMessage(Utils.getMessage("messages.gamemode.changed-other-2", player)
                .replace("%gamemode%", Utils.getMessage("messages.gamemode." + gameMode.name(), from)));
        if (!from.equals(player)) {
            String changedOther = Utils.getMessage("messages.gamemode.changed-other", from)
                    .replace("%gamemode%", Utils.getMessage("messages.gamemode." + gameMode.name(), from))
                    .replace("%other%", player.getName());
            from.sendMessage(changedOther);
        }
    }

}
