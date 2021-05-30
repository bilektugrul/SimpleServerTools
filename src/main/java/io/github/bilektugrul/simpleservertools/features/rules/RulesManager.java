package io.github.bilektugrul.simpleservertools.features.rules;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.upperlevel.spigot.book.BookUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RulesManager {

    public final SST plugin;
    public FileConfiguration file;

    public int splitRulesEvery;
    double pageCount;
    public List<String> rules;
    public RuleMode ruleMode;

    public RulesManager(SST plugin) {
        this.plugin = plugin;
        reloadRules();
    }

    public void reloadRules() {
        file = ConfigUtils.getConfig(plugin, "rules");
        rules = file.getStringList("lines");
        splitRulesEvery = file.getInt("split-rules-every");
        pageCount = (double) rules.size() / splitRulesEvery;
        pageCount = Math.ceil(pageCount);
        ruleMode = RuleMode.valueOf(file.getString("mode").toUpperCase(Locale.ROOT));
    }

    public List<List<String>> setupRulePages(CommandSender sender) {
        List<List<String>> pages =  new ArrayList<>();
        for (int i = 1; i <= pageCount; i++) {
            pages.add(getRulesPage(i));
        }
        return pages;
    }

    public String pageToString(List<String> list) {
        StringBuilder builder = new StringBuilder();
        for (String s : list) {
            builder.append(s).append('\n');
        }
        return builder.toString();
    }

    public <T> List<T> page(List<T> list, int page, int length) {
        int size = list.size();
        return list.subList(Math.min((page - 1) * length, size), Math.min(page * length, size));
    }

    public List<String> getRulesPage(int page) {
        page = Math.max(1, page);
        return page(rules, page, splitRulesEvery);
    }

    public void sendRules(CommandSender sender, int page) {
        if (rules.isEmpty()) {
            sender.sendMessage(Utils.getString(file, "no-rule", sender));
            return;
        }

        if (ruleMode == RuleMode.BOOK && sender instanceof Player) {
            openRuleBook((Player) sender);
        } else {
            sender.sendMessage(Utils.getString(file, "prefix", sender));
            for (String s : getRulesPage(page)) {
                sender.sendMessage(Utils.replacePlaceholders(s, sender, true));
            }
            sender.sendMessage(Utils.getString(file, "suffix", sender));
        }
    }

    public void openRuleBook(Player player) {
        List<List<String>> pages = setupRulePages(player);
        List<BaseComponent[]> pageComponents = new ArrayList<>();
        for (List<String> page : pages) {
            String pageString = Utils.replacePlaceholders(pageToString(page), player, true);
            pageComponents.add(new BaseComponent[]{new TextComponent(pageString)});
        }
        ItemStack book = BookUtil.writtenBook()
                .pages(pageComponents)
                .build();
        BookUtil.openPlayer(player, book);
    }

    public enum RuleMode {

        BOOK, CHAT

    }

}
