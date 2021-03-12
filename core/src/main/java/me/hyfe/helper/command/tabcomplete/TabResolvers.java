package me.hyfe.helper.command.tabcomplete;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TabResolvers {
    private static final Map<Class<?>, TabResolver> TYPES = new HashMap<Class<?>, TabResolver>() {{
        this.put(Player.class, (sender) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
    }};

    public static TabResolver getResolver(Class<?> clazz) {
        return TYPES.get(clazz);
    }

    public static void addResolver(Class<?> clazz, TabResolver resolver) {
        TYPES.put(clazz, resolver);
    }
}
