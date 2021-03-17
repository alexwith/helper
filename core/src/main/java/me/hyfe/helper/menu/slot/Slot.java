package me.hyfe.helper.menu.slot;

import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class Slot {
    private final Gui gui;
    private final int id;
    protected final Map<ClickType, Set<Consumer<InventoryClickEvent>>> handlers;

    public Slot(Gui gui, int id) {
        this.gui = gui;
        this.id = id;
        this.handlers = Collections.synchronizedMap(new EnumMap<>(ClickType.class));
    }

    public void handle(InventoryClickEvent event) {
        Set<Consumer<InventoryClickEvent>> handlers = this.handlers.get(event.getClick());
        if (handlers == null) {
            return;
        }
        for (Consumer<InventoryClickEvent> handler : handlers) {
            try {
                handler.accept(event);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public Gui gui() {
        return this.gui;
    }

    public int getId() {
        return this.id;
    }

    public Slot applyItem(Item item) {
        this.setItem(item.getItemStack());
        this.clearBindings();
        this.bindAllConsumers(item.getHandlers().entrySet());
        return this;
    }

    public ItemStack getItem() {
        return this.gui.getInventory().getItem(this.id);
    }

    public boolean hasItem() {
        return this.getItem() != null;
    }

    public Slot setItem(ItemStack item) {
        this.gui.getInventory().setItem(this.id, item);
        return this;
    }

    public Slot clear() {
        this.clearItem();
        this.clearBindings();
        return this;
    }

    public Slot clearItem() {
        this.gui.getInventory().clear(this.id);
        return this;
    }

    public Slot clearBindings() {
        this.handlers.clear();
        return this;
    }

    public Slot clearBindings(ClickType type) {
        this.handlers.remove(type);
        return this;
    }

    public Slot bind(ClickType type, Consumer<InventoryClickEvent> handler) {
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(handler);
        return this;
    }

    public Slot bind(ClickType type, Runnable handler) {
        this.handlers.computeIfAbsent(type, t -> ConcurrentHashMap.newKeySet()).add(Item.transformRunnable(handler));
        return this;
    }

    public Slot bind(Consumer<InventoryClickEvent> handler, ClickType... types) {
        for (ClickType type : types) {
            this.bind(type, handler);
        }
        return this;
    }

    public Slot bind(Runnable handler, ClickType... types) {
        for (ClickType type : types) {
            this.bind(type, handler);
        }
        return this;
    }

    public <T extends Runnable> Slot bindAllRunnables(Iterable<Map.Entry<ClickType, T>> handlers) {
        for (Map.Entry<ClickType, T> handler : handlers) {
            this.bind(handler.getKey(), handler.getValue());
        }
        return this;
    }

    public <T extends Consumer<InventoryClickEvent>> Slot bindAllConsumers(Iterable<Map.Entry<ClickType, T>> handlers) {
        for (Map.Entry<ClickType, T> handler : handlers) {
            this.bind(handler.getKey(), handler.getValue());
        }
        return this;
    }
}