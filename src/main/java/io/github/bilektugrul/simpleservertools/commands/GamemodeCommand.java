package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

// UNCOMPLETED AND BAD
public class GamemodeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        GameMode gamemode;
        Player p2;
        boolean ifPlayer = sender instanceof Player;
        if (args.length >= 1) {
            gamemode = matchGameMode(args[0]);
            if (gamemode == null) {
                gamemode = matchGameMode(label);
                if (gamemode == null) {
                    if (ifPlayer) Utils.getString("other-messages.gamemode.not-found", (Player) sender);
                    else Utils.getPAPILessString("other-messages.gamemode.not-found", sender);
                } else {
                    p2 = Bukkit.getPlayer(args[0]);
                    if (p2 == null) {
                        if (ifPlayer) Utils.getString("other-messages.gamemode.player-not-found", (Player) sender);
                        else Utils.getPAPILessString("other-messages.gamemode.player-not-found", sender);
                    } else {
                        if (ifPlayer) setGamemode(sender, p2, gamemode);
                    }

                }
            }
        } else if (ifPlayer) {
            gamemode = matchGameMode(label);
            if (gamemode == null) {
                Utils.getString("other-messages.gamemode.not-found", (Player) sender);
            } else {
                setGamemode(sender, (Player) sender, gamemode);
            }
        } else {
            Utils.getPAPILessString("other-messages.gamemode.wrong-arguments", sender);
        }
        return true;
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

    private void setGamemode(CommandSender p, Player p2, GameMode mode) {
        try {
            if (canChange(p, mode)) {
                p2.setGameMode(mode);
                if (p instanceof Player) {
                    Player p3  = (Player) p;
                    if (p3.equals(p2)) {
                        p3.sendMessage(Utils.getString("other-messages.gamemode.changed", p3)
                                .replace("%gamemode%", Utils.getPAPILessString("other-messages.gamemode." + mode.name())));
                    } else {
                        String changedOther = Utils.getString("other-messages.gamemode.changed-other", p3)
                                .replace("%gamemode%", Utils.getPAPILessString("other-messages.gamemode." + mode.name()));
                        p2.sendMessage(Utils.getString("other-messages.gamemode.changed", p2)
                                .replace("%gamemode%", Utils.getPAPILessString("other-messages.gamemode." + mode.name())));
                        p3.sendMessage(changedOther);
                    }
                } else {
                    String changedOther = Utils.getPAPILessString("other-messages.gamemode.changed-other", p)
                            .replace("%other%", p2.getName())
                            .replace("%gamemode%", Utils.getPAPILessString("other-messages.gamemode." + mode.name()));
                    p2.sendMessage(Utils.getString("other-messages.gamemode.changed", p2)
                            .replace("%gamemode%", Utils.getPAPILessString("other-messages.gamemode." + mode.name())));
                    p.sendMessage(changedOther);
                }
            }
        } catch (Exception ignored) {
        }
    }

    private boolean canChange(CommandSender sender, GameMode gamemode) {
        return sender.hasPermission("sst.gamemode.*") || sender.hasPermission("sst.gamemode." + gamemode.name().toLowerCase(Locale.ROOT));
    }

}
