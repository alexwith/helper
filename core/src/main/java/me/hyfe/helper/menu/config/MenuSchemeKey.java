package me.hyfe.helper.menu.config;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.config.keys.ConfigKey;
import me.hyfe.helper.menu.scheme.MenuScheme;

import java.util.List;

public class MenuSchemeKey extends ConfigKey<MenuScheme> {

    public MenuSchemeKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static MenuSchemeKey ofKey(Class<? extends KeysHolder> keysHolder, String key) {
        return new MenuSchemeKey(keysHolder, key);
    }

    @Override
    public MenuScheme get() {
        List<String> masks = this.getConfig().tryGet(this.getKey());
        MenuScheme menuScheme = new MenuScheme();
        for (String mask : masks) {
            menuScheme.mask(mask);
        }
        return menuScheme;
    }
}
