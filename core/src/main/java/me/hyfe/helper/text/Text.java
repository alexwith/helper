package me.hyfe.helper.text;

import org.bukkit.command.CommandSender;

public class Text {

    public static void send(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    public static void send(CommandSender sender, String... messages) {
        for (String message : messages) {
            send(sender, message);
        }
    }
}
