package me.hyfe.helper.command.argument;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ArgumentTypes {
    private static final Map<Class<?>, ArgumentType<?>> TYPES = new HashMap<Class<?>, ArgumentType<?>>() {{
        this.put(Player.class, Bukkit::getPlayerExact);
        this.put(OfflinePlayer.class, Bukkit::getOfflinePlayer);
        this.put(Integer.class, Integer::parseInt);
        this.put(Double.class, Double::parseDouble);
    }};

    @SuppressWarnings("unchecked")
    public static <T> ArgumentType<T> getType(Class<T> clazz) {
        return (ArgumentType<T>) TYPES.get(clazz);
    }

    public static <T> void addType(Class<T> clazz, ArgumentType<T> type) {
        TYPES.put(clazz, type);
    }
}
