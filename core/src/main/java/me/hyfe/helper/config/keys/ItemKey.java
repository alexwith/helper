package me.hyfe.helper.config.keys;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.item.ItemStackBuilder;
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
        return ItemStackBuilder.of(this.getConfig(), this.getKey()).build();
    }
}
