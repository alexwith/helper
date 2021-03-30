package me.hyfe.helper.text;

import me.hyfe.helper.text.replacer.Replacer;
import me.hyfe.helper.text.replacer.Subject;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    public static ItemStack colorize(ItemStack itemStack, Replacer replacer) {
        if (replacer == null) {
            return itemStack;
        }
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return itemStack;
        }
        meta.setDisplayName(colorize(meta.getDisplayName(), replacer));
        meta.setLore(colorize(meta.getLore(), replacer));
        item.setItemMeta(meta);
        return item;
    }

    public static List<String> colorize(Collection<String> collection) {
        return colorize(collection, null);
    }

    public static List<String> colorize(Collection<String> collection, Replacer replacer) {
        return collection.stream().map((string) -> colorize(string, replacer)).collect(Collectors.toList());
    }

    public static String colorize(String string) {
        return colorize(string, null);
    }

    public static String colorize(String string, Replacer replacer) {
        return ChatColor.translateAlternateColorCodes('&', replace(string, replacer));
    }

    public static String replace(String string, Replacer replacer) {
        return replacer == null ? string : replacer.apply(new Subject()).applyTo(string);
    }
}
