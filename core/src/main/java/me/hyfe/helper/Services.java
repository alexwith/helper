package me.hyfe.helper;

import me.hyfe.helper.internal.LoaderUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;

import java.util.Objects;
import java.util.Optional;

public final class Services {

    public static <T> T load(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        return get(clazz).orElseThrow(() -> new IllegalStateException("No registration present for service '" + clazz.getName() + "'"));
    }

    public static <T> Optional<T> get(Class<T> clazz) {
        Objects.requireNonNull(clazz, "clazz");
        RegisteredServiceProvider<T> registration = Bukkit.getServicesManager().getRegistration(clazz);
        if (registration == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(registration.getProvider());
    }

    public static <T> T provide(Class<T> clazz, T instance, Plugin plugin, ServicePriority priority) {
        Objects.requireNonNull(clazz, "clazz");
        Objects.requireNonNull(instance, "instance");
        Objects.requireNonNull(plugin, "plugin");
        Objects.requireNonNull(priority, "priority");
        Bukkit.getServicesManager().register(clazz, instance, plugin, priority);
        return instance;
    }

    public static <T> T provide(Class<T> clazz, T instance, ServicePriority priority) {
        return provide(clazz, instance, LoaderUtils.getPlugin(), priority);
    }

    public static <T> T provide(Class<T> clazz, T instance) {
        return provide(clazz, instance, ServicePriority.Normal);
    }
}