package me.hyfe.helper.config;

import me.hyfe.helper.promise.Promise;

import java.util.HashMap;
import java.util.Map;

public class ConfigController {
    private final Map<String, Config> configs = new HashMap<>();

    public Config get(String name) {
        return this.configs.get(name);
    }

    public Promise<Void> reload(String name) {
        return this.configs.get(name).reload();
    }

    public void registerConfigs(KeysHolder... keyHolders) {
        for (KeysHolder keysHolder : keyHolders) {
            Config config = keysHolder.getConfig();
            this.configs.put(config.getName(), config);
        }
    }
}