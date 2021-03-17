package me.hyfe.helper.menu.config;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.config.keys.ItemKey;
import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;

import java.util.function.UnaryOperator;

public class GuiItemKey extends ItemKey {

    public GuiItemKey(Class<? extends KeysHolder> keysHolder, String key) {
        super(keysHolder, key);
    }

    public static GuiItemKey ofKey(Class<? extends KeysHolder> keysHolder, String key) {
        return new GuiItemKey(keysHolder, key);
    }

    public Item.Builder getBuilder() {
        return Item.builder(super.get());
    }

    public void toGui(Gui gui, UnaryOperator<Item.Builder> builder) {
        Item item = builder.apply(this.getBuilder()).build();
        int slot = this.getConfig().tryGet(this.getKey().concat(".slot"));
        gui.setItem(item, slot);
    }
}
