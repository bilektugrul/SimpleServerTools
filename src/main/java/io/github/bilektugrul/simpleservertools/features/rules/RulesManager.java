package io.github.bilektugrul.simpleservertools.features.rules;

import io.github.bilektugrul.simpleservertools.SST;
import io.github.bilektugrul.simpleservertools.utils.Utils;
import me.despical.commons.configuration.ConfigUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
    public boolean pageSelectorEnabled;
    public String prefix;
    public String suffix;

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
        pageSelectorEnabled = file.getBoolean("page-selector.enabled");
        prefix = file.getString("prefix");
        suffix = file.getString("suffix");
    }

    public List<List<String>> setupRulePages() {
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
        page = Math.max(1, page);
        if (rules.isEmpty()) {
            sender.sendMessage(Utils.getString(file, "no-rule", sender));
            return;
        }
        if (ruleMode == RuleMode.BOOK && sender instanceof Player p) {
            openRuleBook(p);
        } else {
            String pageString = String.valueOf(page);
            sendComponent(sender, page, pageString);

            if (!prefix.isEmpty()) {
                String coloredPrefix = Utils.replacePlaceholders(prefix, sender, true);
                sender.sendMessage(coloredPrefix.replace("%page%", pageString));
            }
            for (String s : getRulesPage(page)) {
                sender.sendMessage(Utils.replacePlaceholders(s, sender, true));
            }
            if (!suffix.isEmpty()) {
                String coloredSuffix = Utils.replacePlaceholders(suffix, sender, true);
                sender.sendMessage(coloredSuffix.replace("%page%", pageString));
            }

            sendComponent(sender, page, pageString);
        }
    }

    public void sendComponent(CommandSender sender, int page, String pageString) {
        if (pageSelectorEnabled) {
            TextComponent component = new TextComponent(Utils.getString(file, "page-selector.back", sender));
            component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rules " + (page - 1)));

            TextComponent middle = new TextComponent(Utils.getString(file, "page-selector.middle", sender)
                    .replace("%page%", pageString));

            BaseComponent next = new TextComponent(Utils.getString(file, "page-selector.next", sender));
            next.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/rules " + (page + 1)));

            component.addExtra(middle);
            component.addExtra(next);
            sender.sendMessage(component);
        }
    }

    public void openRuleBook(Player player) {
        List<List<String>> pages = setupRulePages();
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
