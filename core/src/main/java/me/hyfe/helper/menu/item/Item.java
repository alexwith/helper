package me.hyfe.helper.menu.item;

import com.google.common.collect.ImmutableMap;
import me.hyfe.helper.text.Text;
import me.hyfe.helper.text.replacer.Replacer;
import me.hyfe.helper.utils.Delegates;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public class Item {

    public static Item.Builder builder(ItemStack itemStack) {
        return new Builder(itemStack);
    }

    private final Map<ClickType, Consumer<InventoryClickEvent>> handlers;
    private final ItemStack itemStack;

    public Item(Map<ClickType, Consumer<InventoryClickEvent>> handlers, ItemStack itemStack) {
        this.handlers = ImmutableMap.copyOf(Objects.requireNonNull(handlers, "handlers"));
        this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
    }

    public Map<ClickType, Consumer<InventoryClickEvent>> getHandlers() {
        return this.handlers;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public static final class Builder {
        private final ItemStack itemStack;
        private final Map<ClickType, Consumer<InventoryClickEvent>> handlers;

        private Builder(ItemStack itemStack) {
            this.itemStack = Objects.requireNonNull(itemStack, "itemStack");
            this.handlers = new HashMap<>();
        }

        public Builder bind(ClickType type, Consumer<InventoryClickEvent> handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, handler);
            } else {
                this.handlers.remove(type);
            }
            return this;
        }

        public Builder bind(Consumer<InventoryClickEvent> handler, ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        public Builder bind(ClickType type, Runnable handler) {
            Objects.requireNonNull(type, "type");
            if (handler != null) {
                this.handlers.put(type, transformRunnable(handler));
            } else {
                this.handlers.remove(type);
            }
            return this;
        }

        public Builder bind(Runnable handler, ClickType... types) {
            for (ClickType type : types) {
                bind(type, handler);
            }
            return this;
        }

        public <T extends Runnable> Builder bindAllRunnables(Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        public <T extends Consumer<InventoryClickEvent>> Builder bindAllConsumers(Iterable<Map.Entry<ClickType, T>> handlers) {
            Objects.requireNonNull(handlers, "handlers");
            for (Map.Entry<ClickType, T> handler : handlers) {
                bind(handler.getKey(), handler.getValue());
            }
            return this;
        }

        public Item build() {
            return this.build(null);
        }

        public Item build(Replacer replacer) {
            return new Item(this.handlers, Text.colorize(this.itemStack, replacer));
        }
    }

    public static Consumer<InventoryClickEvent> transformRunnable(Runnable runnable) {
        return Delegates.runnableToConsumer(runnable);
    }
}