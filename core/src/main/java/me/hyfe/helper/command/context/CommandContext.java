package me.hyfe.helper.command.context;

import me.hyfe.helper.command.command.Command;
import org.bukkit.command.CommandSender;

public class CommandContext {
    private final Command<?> command;
    protected final String[] args;

    public CommandContext(Command<?> command, String[] args) {
        this.command = command;
        this.args = args;
    }

    public void sendUsage(CommandSender sender) {
        this.command.sendUsage(sender);
    }
}
