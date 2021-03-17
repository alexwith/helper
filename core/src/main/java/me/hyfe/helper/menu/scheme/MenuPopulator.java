package me.hyfe.helper.menu.scheme;

import com.google.common.collect.ImmutableList;
import me.hyfe.helper.menu.gui.Gui;
import me.hyfe.helper.menu.item.Item;
import me.hyfe.helper.menu.slot.Slot;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public class MenuPopulator {
    private final Gui gui;
    private final ImmutableList<Integer> slots;
    protected List<Integer> remainingSlots;

    public MenuPopulator(Gui gui, MenuScheme scheme) {
        this.remainingSlots = scheme.getMaskedIndexes();
        this.gui = gui;
        this.slots = ImmutableList.copyOf(this.remainingSlots);
    }

    public MenuPopulator(Gui gui, List<Integer> slots) {
        this.gui = gui;
        this.slots = ImmutableList.copyOf(slots);
        this.reset();
    }

    private MenuPopulator(MenuPopulator other) {
        this.gui = other.gui;
        this.slots = other.slots;
        this.reset();
    }

    public ImmutableList<Integer> getSlots() {
        return this.slots;
    }

    public void reset() {
        this.remainingSlots = new LinkedList<>(this.slots);
    }

    public MenuPopulator consume(Consumer<Slot> action) {
        if (this.tryConsume(action)) {
            return this;
        } else {
            throw new IllegalStateException("No more slots");
        }
    }

    public MenuPopulator consumeIfSpace(Consumer<Slot> action) {
        this.tryConsume(action);
        return this;
    }

    public boolean tryConsume(Consumer<Slot> action) {
        if (this.remainingSlots.isEmpty()) {
            return false;
        }

        int slot = this.remainingSlots.remove(0);
        action.accept(this.gui.getSlot(slot));
        return true;
    }

    public MenuPopulator accept(Item item) {
        return this.consume(slot -> slot.applyItem(item));
    }

    public MenuPopulator acceptIfSpace(Item item) {
        return this.consumeIfSpace(slot -> slot.applyItem(item));
    }

    public boolean placeIfSpace(Item item) {
        return this.tryConsume(slot -> slot.applyItem(item));
    }

    public int getRemainingSpace() {
        return this.remainingSlots.size();
    }

    public boolean hasSpace() {
        return !this.remainingSlots.isEmpty();
    }

    public MenuPopulator copy() {
        return new MenuPopulator(this);
    }
}