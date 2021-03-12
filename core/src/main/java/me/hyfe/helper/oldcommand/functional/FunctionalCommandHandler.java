package me.hyfe.helper.oldcommand.functional;

import me.hyfe.helper.oldcommand.CommandInterruptException;
import me.hyfe.helper.oldcommand.context.CommandContext;
import org.bukkit.command.CommandSender;

public interface FunctionalCommandHandler<T extends CommandSender>  {

    void handle(CommandContext<T> context) throws CommandInterruptException;
}
