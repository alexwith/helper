package me.hyfe.helper.command.command;

import me.hyfe.helper.command.context.CommandContext;
import me.hyfe.helper.internal.LoaderUtils;
import me.hyfe.helper.plugin.HelperPlugin;
import me.hyfe.helper.utils.Translate;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AbstractCommand<T extends CommandSender> {
    protected final Class<T> senderClass;

    protected final HelperPlugin plugin;
    protected final String permission;
    protected final String description;

    public AbstractCommand(Class<T> senderClass, String permission, String description) {
        this.plugin = LoaderUtils.getPlugin();
        this.senderClass = senderClass;
        this.permission = permission;
        this.description = description;
    }

    public abstract void handle(T sender, CommandContext context);

    public String getPermission() {
        return this.permission;
    }

    public String getDescription() {
        return this.description;
    }

    public boolean canConsole() {
        return !this.senderClass.equals(Player.class);
    }
}
