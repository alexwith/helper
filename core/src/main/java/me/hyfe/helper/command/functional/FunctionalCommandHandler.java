package me.hyfe.helper.command.functional;

import me.hyfe.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;

public interface FunctionalCommandHandler<T extends CommandSender, U extends CommandContext> {

    void handle(T sender, U context);
}
