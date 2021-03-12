package me.hyfe.helper.config.keys;

import me.hyfe.helper.config.Config;
import me.hyfe.helper.config.KeysHolder;
import org.bukkit.inventory.ItemStack;

public class ItemKey extends ConfigKey<ItemStack> {

    public ItemKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static ItemKey ofKey(Class<? extends KeysHolder> keysHolder, String key) {
        return new ItemKey(keysHolder, key);
    }

    @Override
    public ItemStack get() {
        Config config = this.getConfig();
        return super.get();
    }
}
