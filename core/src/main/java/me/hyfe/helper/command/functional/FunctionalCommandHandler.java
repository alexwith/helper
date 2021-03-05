package me.hyfe.helper.command.functional;

import me.hyfe.helper.command.CommandInterruptException;
import me.hyfe.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface FunctionalCommandHandler<T extends CommandSender>  {

    void handle(CommandContext<T> c) throws CommandInterruptException;
}