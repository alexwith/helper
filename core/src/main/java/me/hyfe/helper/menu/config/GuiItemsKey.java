package me.hyfe.helper.menu.config;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.config.keys.ConfigKey;
import me.hyfe.helper.item.ItemStackBuilder;
import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class GuiItemsKey extends ConfigKey<List<Item.Builder>> {

    public GuiItemsKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static GuiItemsKey ofKey(Class<? extends KeysHolder> keysHolder, String key) {
        return new GuiItemsKey(keysHolder, key);
    }

    @Override
    public List<Item.Builder> get() {
        List<Item.Builder> builders = new ArrayList<>();
        for (String key : this.getConfig().getKeys(this.getKey())) {
            builders.add(Item.builder(ItemStackBuilder.of(this.getConfig(), this.getKey() + "." + key + ".item").build()));
        }
        return builders;
    }

    public void toGui(Gui gui, UnaryOperator<Item.Builder> modifier) {
        for (String key : this.getConfig().getKeys(this.getKey())) {
            Item.Builder builder = Item.builder(ItemStackBuilder.of(this.getConfig(), this.getKey() + "." + key + ".item").build());
            Item item = modifier.apply(builder).build();
            int slot = this.getConfig().tryGet(this.getKey() + "." + key + ".slot");
            gui.setItem(item, slot);
        }
    }
}