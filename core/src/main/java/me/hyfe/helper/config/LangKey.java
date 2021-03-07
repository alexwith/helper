package me.hyfe.helper.config;

import me.hyfe.helper.text.Text;
import me.hyfe.helper.text.replacer.Replacer;
import org.bukkit.command.CommandSender;

public class LangKey extends ConfigKey<String> {

    public LangKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static LangKey ofLang(Class<? extends KeysHolder> keysHolder, String key) {
        return new LangKey(keysHolder, key);
    }

    public void send(CommandSender commandSender) {
        this.send(commandSender, null);
    }

    public void send(CommandSender commandSender, Replacer replacer) {
        Text.send(commandSender, this.get(), replacer);
    }
}
