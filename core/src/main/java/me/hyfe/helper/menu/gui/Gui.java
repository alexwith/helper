package me.hyfe.helper.menu.gui;

import me.hyfe.helper.Events;
import me.hyfe.helper.Schedulers;
import me.hyfe.helper.terminable.TerminableConsumer;
import me.hyfe.helper.terminable.composite.CompositeTerminable;
import me.hyfe.helper.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.function.Function;

public abstract class Gui implements InventoryHolder, TerminableConsumer {
    protected final Player player;
    protected String title;
    protected int rows;
    protected Inventory inventory;
    protected boolean firstDraw = true;

    private Function<Player, Gui> fallback;

    private boolean valid;
    private final CompositeTerminable compositeTerminable = CompositeTerminable.create();

    public Gui(Player player, String title, int rows) {
        this.player = player;
        this.title = Text.colorize(title);
        this.rows = rows;
        this.createInventory();
    }

    public Gui(Player player) {
        this.player = player;
    }

    public abstract void redraw();

    public void title(String title) {
        this.title = title;
    }

    public void rows(int rows) {
        this.rows = rows;
    }

    public void createInventory() {
        this.inventory = Bukkit.createInventory(this, this.rows * 9, this.title);
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public <T extends AutoCloseable> T bind(T terminable) {
        return this.compositeTerminable.bind(terminable);
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getTitle() {
        return this.title;
    }

    public Function<Player, Gui> getFallback() {
        return this.fallback;
    }

    public void setFallback(Function<Player, Gui> fallback) {
        this.fallback = fallback;
    }

    public boolean isFirstDraw() {
        return this.firstDraw;
    }

    public void clear() {

    }

    public void open() {
        this.firstDraw = true;
        try {
            this.redraw();
        } catch (Exception ex) {
            ex.printStackTrace();
            this.invalidate();
            return;
        }
        this.firstDraw = false;
        this.listeners();
        this.player.openInventory(this.inventory);
        this.valid = true;
    }

    public void close() {
        this.player.closeInventory();
    }

    private void invalidate() {
        this.valid = false;
        this.compositeTerminable.closeAndReportException();
        this.clear();
    }

    private void listeners() {
        Events.merge(Player.class)
                .bindEvent(PlayerDeathEvent.class, PlayerDeathEvent::getEntity)
                .bindEvent(PlayerQuitEvent.class, PlayerEvent::getPlayer)
                .bindEvent(PlayerChangedWorldEvent.class, PlayerEvent::getPlayer)
                .bindEvent(PlayerTeleportEvent.class, PlayerEvent::getPlayer)
                .filter((player) -> player.equals(this.player))
                .filter((player) -> this.valid)
                .handler((player) -> this.invalidate())
                .bindWith(this);
        Events.subscribe(InventoryDragEvent.class)
                .filter((event) -> event.getInventory().getHolder() != null)
                .filter((event) -> event.getInventory().getHolder().equals(this))
                .handler((event) -> {
                    event.setCancelled(true);
                    if (this.valid) {
                        this.close();
                    }
                }).bindWith(this);
        Events.subscribe(InventoryClickEvent.class)
                .filter((event) -> event.getInventory().getHolder() != null)
                .filter((event) -> event.getInventory().getHolder().equals(this))
                .handler((event) -> {
                    event.setCancelled(true);
                    if (!valid) {
                        this.close();
                        return;
                    }
                    int slodId = event.getSlot();
                    if (slodId != event.getSlot()) {
                        return;
                    }

                }).bindWith(this);
        Events.subscribe(InventoryOpenEvent.class)
                .filter((event) -> event.getPlayer().equals(this.player))
                .filter((event) -> event.getInventory().getHolder() != null)
                .filter((event) -> event.getInventory().getHolder().equals(this))
                .filter((event) -> this.valid)
                .handler((event) -> this.invalidate())
                .bindWith(this);
        Events.subscribe(InventoryCloseEvent.class)
                .filter((event) -> event.getPlayer().equals(this.player))
                .filter((event) -> event.getInventory().getHolder() != null)
                .filter((event) -> event.getInventory().getHolder().equals(this))
                .handler((event) -> {
                    this.invalidate();
                    Function<Player, Gui> fallback = this.fallback;
                    if (fallback == null) {
                        return;
                    }
                    Schedulers.sync().runLater(() -> {
                        if (!this.player.isOnline()) {
                            return;
                        }
                        Gui gui = fallback.apply(this.player);
                        if (gui == null || gui.valid) {
                            return;
                        }
                        gui.open();
                    }, 1);
                }).bindWith(this);
    }
}
