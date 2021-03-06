package me.hyfe.helper;

import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.plugin.HelperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.Optional;

public final class Helper {

    public static HelperPlugin hostPlugin() {
        return LoaderUtils.getPlugin();
    }

    public static Server server() {
        return Bukkit.getServer();
    }

    public static ConsoleCommandSender console() {
        return server().getConsoleSender();
    }

    public static PluginManager plugins() {
        return server().getPluginManager();
    }

    public static ServicesManager services() {
        return server().getServicesManager();
    }

    public static BukkitScheduler bukkitScheduler() {
        return server().getScheduler();
    }

    public static <T> T serviceNullable(Class<T> clazz) {
        return Services.get(clazz).orElse(null);
    }

    public static <T> Optional<T> service(Class<T> clazz) {
        return Services.get(clazz);
    }

    public static void executeCommand(String command) {
        server().dispatchCommand(console(), command);
    }

    public static World worldNullable(String name) {
        return server().getWorld(name);
    }

    public static World world(String name) {
        return worldNullable(name);
    }
}