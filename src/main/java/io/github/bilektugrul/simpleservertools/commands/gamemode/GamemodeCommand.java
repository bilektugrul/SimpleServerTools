package io.github.bilektugrul.simpleservertools.commands.gamemode;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        StringBuilder builder = new StringBuilder();
        builder.append(label).append(" ");
        builder.append(Utils.arrayToString(args, sender, false, false));
        GamemodeInfo gamemodeInfo = matchInfo(builder.toString(), sender);
        if (gamemodeInfo.isCompleted()) {
            if (canChange(sender, gamemodeInfo.gameMode)) {
                gamemodeInfo.apply(sender);
            } else {
                sender.sendMessage(Utils.getString("no-permission", sender));
            }
        } else {
            sender.sendMessage(Utils.getString("other-messages.gamemode.wrong-arguments", sender));
        }
        return true;
    }

    private GamemodeInfo matchInfo(String fullCommand, CommandSender sender) {
        String[] split = fullCommand.split(" ");
        GamemodeInfo gamemodeInfo = new GamemodeInfo();
        for (String s : split) {
            GameMode gamemode = matchGameMode(s);
            if (gamemode != null) {
                gamemodeInfo.setGameMode(gamemode);
            } else {
                Player p = Bukkit.getPlayer(s);
                if (p != null) {
                    gamemodeInfo.setPlayer(p);
                }
            }
        }
        if (gamemodeInfo.player == null && sender instanceof Player) gamemodeInfo.setPlayer((Player) sender);
        return gamemodeInfo;
    }

    //Source: https://github.com/EssentialsX/Essentials/blob/2.x/Essentials/src/main/java/com/earth2me/essentials/commands/Commandgamemode.java - EssentialsX
    private GameMode matchGameMode(String mode) {
        mode = mode.toLowerCase();
        if (mode.equalsIgnoreCase("gmc") || mode.equalsIgnoreCase("egmc") || mode.contains("creat") || mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c")) {
            return GameMode.CREATIVE;
        } else if (mode.equalsIgnoreCase("gms") || mode.equalsIgnoreCase("egms") || mode.contains("survi") || mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s")) {
            return GameMode.SURVIVAL;
        } else if (mode.equalsIgnoreCase("gma") || mode.equalsIgnoreCase("egma") || mode.contains("advent") || mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a")) {
            return GameMode.ADVENTURE;
        } else if (mode.equalsIgnoreCase("gmsp") || mode.equalsIgnoreCase("egmsp") || mode.contains("spec") || mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("sp")) {
            return GameMode.SPECTATOR;
        }
        return null;
    }

    private boolean canChange(CommandSender sender, GameMode gamemode) {
        return sender.hasPermission("sst.gamemode.*") || sender.hasPermission("sst.gamemode." + gamemode.name().toLowerCase(Locale.ROOT));
    }

}