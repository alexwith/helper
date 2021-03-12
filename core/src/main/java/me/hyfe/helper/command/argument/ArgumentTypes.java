package me.hyfe.helper.command.argument;

import me.hyfe.helper.Helper;
import me.hyfe.helper.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ArgumentTypes {
    private static final Map<Class<?>, ArgumentType<?>> TYPES = new HashMap<Class<?>, ArgumentType<?>>() {{
        this.put(Player.class, Bukkit::getPlayerExact);
        this.put(OfflinePlayer.class, Bukkit::getOfflinePlayer);
        this.put(World.class, Helper::world);
        this.put(Integer.class, Integer::parseInt);
        this.put(Double.class, Double::parseDouble);
        this.put(Long.class, Long::parseLong);
        this.put(Boolean.class, arg -> arg.equalsIgnoreCase("true"));
        this.put(UUID.class, FastUUID::parse);
    }};

    @SuppressWarnings("unchecked")
    public static <T> ArgumentType<T> getType(Class<T> clazz) {
        return (ArgumentType<T>) TYPES.get(clazz);
    }

    public static <T> void addType(Class<T> clazz, ArgumentType<T> type) {
        TYPES.put(clazz, type);
    }
}
