package me.hyfe.helper.plugin;

import me.hyfe.helper.command.argument.ArgumentTypes;
import me.hyfe.helper.command.tabcomplete.TabResolvers;
import me.hyfe.helper.config.Config;
import me.hyfe.helper.config.ConfigController;
import me.hyfe.helper.internal.LoaderUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class HelperPlugin extends JavaPlugin {
    protected final ConfigController configController;
    protected final ArgumentTypes.Holder argumentTypes;
    protected final TabResolvers.Holder tabResolvers;

    public HelperPlugin() {
        this.configController = new ConfigController();
        this.argumentTypes = new ArgumentTypes.Holder();
        this.tabResolvers = new TabResolvers.Holder();
    }

    protected void enable() {
    }

    protected void disable() {
    }

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

    public ArgumentTypes.Holder getArgumentTypes() {
        return this.argumentTypes;
    }

    public TabResolvers.Holder getTabResolvers() {
        return this.tabResolvers;
    }

    public Config getConfig(String name) {
        return this.configController.get(name);
    }
}
