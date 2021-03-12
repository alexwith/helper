package me.hyfe.helper.command.functional;

import org.bukkit.command.CommandSender;

public abstract class AbstractCommandBuilder<T extends CommandSender> {
    protected Class<?> senderClass;
    protected String permission;
    protected String description;

    public AbstractCommandBuilder(Class<?> senderClass, String permission, String description) {
        this.senderClass = senderClass;
        this.permission = permission;
        this.description = description;
    }
}