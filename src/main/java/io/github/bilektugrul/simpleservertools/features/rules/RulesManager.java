package io.github.bilektugrul.simpleservertools.features.rules;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class RulesManager {

    public final SST plugin;

    public List<String> rules;
    public int lineCount;
    public FileConfiguration rulesFile;

    public RulesManager(SST plugin) {
        this.plugin = plugin;
        reloadRules();
    }

    public void reloadRules() {
        rulesFile = ConfigUtils.getConfig(plugin, "rules");
        rules = rulesFile.getStringList("rules");
        lineCount = rulesFile.getInt("split-rules-every");
    }

    public void sendRules(CommandSender sender, int page) {
        if (rules.isEmpty()) {
            sender.sendMessage(Utils.getString(rulesFile, "no-rule", sender, true, true));
            return;
        }

        int startLine = Math.max(0, lineCount * page - lineCount + 1);
        int size = rules.size();

        if (startLine >= size) {
            startLine = size - (lineCount - 1);
        }

        sender.sendMessage(Utils.getString(rulesFile, "rules-prefix", sender, true, true));
        for (int i = 0; i < lineCount; i++) {
            sender.sendMessage(Utils.replacePlaceholders(rules.get(startLine), sender, true, true));
            startLine++;
            if (startLine == size) break; // fuck off
        }
        sender.sendMessage(Utils.getString(rulesFile, "rules-suffix", sender, true, true));
    }

}
