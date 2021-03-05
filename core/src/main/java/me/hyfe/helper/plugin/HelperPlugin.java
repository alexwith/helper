package me.hyfe.helper.plugin;

import me.hyfe.helper.internal.LoaderUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class HelperPlugin extends JavaPlugin {

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
}
