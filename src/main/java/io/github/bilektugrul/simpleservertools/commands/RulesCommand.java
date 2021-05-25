package io.github.bilektugrul.simpleservertools.commands;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.features.rules.RulesManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class RulesCommand implements CommandExecutor {

    private final RulesManager rulesManager;

    public RulesCommand(SST plugin) {
        this.rulesManager = plugin.getRulesManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        int page = 0;
        try {
            page = Integer.parseInt(args[0]);
        } catch (Exception ignored) { }
        if (page < 0) page = 0;
        rulesManager.sendRules(sender, page);
        return true;
    }

}
