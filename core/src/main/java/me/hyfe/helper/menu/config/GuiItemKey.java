package me.hyfe.helper.menu.config;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.config.keys.ConfigKey;
import me.hyfe.helper.item.ItemStackBuilder;
import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;
import me.hyfe.helper.text.replacer.Replacer;

import java.util.function.UnaryOperator;

public class GuiItemKey extends ConfigKey<Item.Builder> {

    public GuiItemKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static GuiItemKey ofKey(Class<? extends KeysHolder> keysHolder, String key) {
        return new GuiItemKey(keysHolder, key);
    }

    @Override
    public Item.Builder get() {
        return Item.builder(ItemStackBuilder.of(this.getConfig(), this.getKey().concat(".item")).build());
    }

    public void toGui(Gui gui, UnaryOperator<Item.Builder> builder) {
        this.toGui(gui, builder, null);
    }

    public void toGui(Gui gui, UnaryOperator<Item.Builder> builder, Replacer replacer) {
        Item item = builder.apply(this.get()).build(replacer);
        int slot = this.getConfig().tryGet(this.getKey().concat(".slot"));
        gui.setItem(item, slot);
    }
}
