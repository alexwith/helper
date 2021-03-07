package me.hyfe.helper.config;

import me.hyfe.helper.text.Text;
import me.hyfe.helper.text.replacer.Replacer;
import org.bukkit.command.CommandSender;

import java.util.function.Supplier;

public class LangKey extends ConfigKey<String> {

    public LangKey(Supplier<KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static LangKey ofLang(Supplier<KeysHolder> keysHolder, String key) {
        return new LangKey(keysHolder, key);
    }

    public void send(CommandSender commandSender) {
        this.send(commandSender, null);
    }

    public void send(CommandSender commandSender, Replacer replacer) {
        Text.send(commandSender, this.get(), replacer);
    }
}
