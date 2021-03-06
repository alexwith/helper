package me.hyfe.helper.text;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Text {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(colorize(message));
    }

    public static void send(CommandSender sender, String... messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
