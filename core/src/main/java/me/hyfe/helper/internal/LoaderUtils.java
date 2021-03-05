package me.hyfe.helper.internal;

import me.hyfe.helper.plugin.HelperPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class LoaderUtils {
    private static HelperPlugin plugin = null;
    private static Thread mainThread = null;

    public static synchronized HelperPlugin getPlugin() {
        if (plugin == null) {
            JavaPlugin plugin = JavaPlugin.getProvidingPlugin(LoaderUtils.class);
            if (!(plugin instanceof HelperPlugin)) {
                throw new IllegalStateException("helper providing plugin does not implement HelperPlugin: " + plugin.getClass().getName());
            }
            LoaderUtils.plugin = (HelperPlugin) plugin;

            String paqkage = LoaderUtils.class.getPackage().getName();
            paqkage = paqkage.substring(0, paqkage.length() - ".internal".length());

            Bukkit.getLogger().info("[helper] helper (" + paqkage + ") bound to plugin " + LoaderUtils.plugin.getName() + " - " + LoaderUtils.plugin.getClass().getName());
        }
        getMainThread();
        return plugin;
    }

    public static synchronized Thread getMainThread() {
        if (mainThread == null) {
            if (Bukkit.getServer().isPrimaryThread()) {
                mainThread = Thread.currentThread();
            }
        }
        return mainThread;
    }
}
