package me.hyfe.helper.config.keys;

import me.hyfe.helper.config.Config;
import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.internal.LoaderUtils;

import java.util.function.Supplier;

public class ConfigKey<T> implements Supplier<T> {
    private final Class<? extends KeysHolder> keysHolder;
    private final String key;

    public ConfigKey(Class<? extends KeysHolder> keysHolder, String key) {
        this.keysHolder = keysHolder;
        this.key = key;
    }

    public static <U> ConfigKey<U> of(Class<? extends KeysHolder> keysHolder, String key) {
        return new ConfigKey<>(keysHolder, key);
    }

    public Config getConfig() {
        return LoaderUtils.getPlugin().getConfigController().getKeysHolder(this.keysHolder).getConfig();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T get() {
        return (T) this.getConfig().get(this.key);
    }
}
