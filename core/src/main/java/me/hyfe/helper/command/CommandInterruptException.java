package me.hyfe.helper.command;

import me.hyfe.helper.text.Text;
import org.bukkit.command.CommandSender;

import java.util.function.Consumer;

public class CommandInterruptException extends Exception {
    
    public static void makeAssertion(boolean condition, String failMsg) throws CommandInterruptException {
        if (!condition) {
            throw new CommandInterruptException(failMsg);
        }
    }

    private final Consumer<CommandSender> action;

    public CommandInterruptException(Consumer<CommandSender> action) {
        this.action = action;
    }

    public CommandInterruptException(String message) {
        this.action = sender -> Text.send(sender, message);
    }

    public Consumer<CommandSender> getAction() {
        return this.action;
    }
}