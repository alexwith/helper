package me.hyfe.helper.command.tabcomplete;

import me.hyfe.helper.internal.LoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class TabResolvers {
    public static class Holder {
        private final Map<Class<?>, TabResolver> types = new HashMap<Class<?>, TabResolver>() {{
            this.put(Player.class, (sender) -> Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList()));
        }};

        public Map<Class<?>, TabResolver> asMap() {
            return this.types;
        }
    }

    public static TabResolver getResolver(Class<?> clazz) {
        return LoaderUtils.getPlugin().getTabResolvers().asMap().get(clazz);
    }

    public static void addResolver(Class<?> clazz, TabResolver resolver) {
        LoaderUtils.getPlugin().getTabResolvers().asMap().put(clazz, resolver);
    }
}
