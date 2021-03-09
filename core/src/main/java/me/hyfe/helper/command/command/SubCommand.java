package me.hyfe.helper.command.command;

import org.bukkit.command.CommandSender;

public class SubCommand<T extends CommandSender> extends AbstractCommand<T> {

    public SubCommand(Class<T> senderClass, String permission, String description) {
        super(senderClass, permission, description);
    }
}
