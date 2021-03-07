package me.hyfe.helper.plugin;

import me.hyfe.helper.config.Config;
import me.hyfe.helper.config.ConfigController;
import me.hyfe.helper.internal.LoaderUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class HelperPlugin extends JavaPlugin {
    protected final ConfigController configController;

    public HelperPlugin() {
        this.configController = new ConfigController();
    }

    protected void enable() {}
    protected void disable() {}

    @Override
    public void onEnable() {
        LoaderUtils.getPlugin();
        this.enable();
    }

    @Override
    public void onDisable() {
        this.disable();
    }

    public ConfigController getConfigController() {
        return this.configController;
    }

    public Config getConfig(String name) {
        return this.configController.get(name);
    }
}
