package me.hyfe.helper.text;

import me.hyfe.helper.text.replacer.Replacer;
import me.hyfe.helper.text.replacer.Subject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Text {

    public static void send(CommandSender sender, String message) {
        send(sender, message, null);
    }

    public static void send(CommandSender sender, String message, Replacer replacer) {
        sender.sendMessage(colorize(message, replacer));
    }

    public static void send(CommandSender sender, String... messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }

    public static String colorize(String string) {
        return colorize(string, null);
    }

    public static String colorize(String string, Replacer replacer) {
        return ChatColor.translateAlternateColorCodes('&', replace(string, null));
    }

    public static String replace(String string, Replacer replacer) {
        return replacer == null ? string : replacer.apply(new Subject()).applyTo(string);
    }
}
