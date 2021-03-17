package me.hyfe.helper.menu.config;

import me.hyfe.helper.config.KeysHolder;
import me.hyfe.helper.config.keys.ConfigKey;
import me.hyfe.helper.menu.gui.Gui;
import org.bukkit.entity.Player;

public abstract class ConfigGui extends Gui {
    private final Class<? extends KeysHolder> keysHolder;

    public ConfigGui(Player player, Class<? extends KeysHolder> keysHolder) {
        super(player);
        this.keysHolder = keysHolder;
        this.readAndWriteMeta();
        this.createInventory();
    }

    @SuppressWarnings("unchecked")
    private void readAndWriteMeta() {
        try {
            this.title(((ConfigKey<String>) this.keysHolder.getField("TITLE").get(null)).get());
            this.rows(((ConfigKey<Integer>) this.keysHolder.getField("ROWS").get(null)).get());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
