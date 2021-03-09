package me.hyfe.helper.oldcommand;

import me.hyfe.helper.oldcommand.context.CommandContext;
import me.hyfe.helper.terminable.Terminable;
import me.hyfe.helper.terminable.TerminableConsumer;

public interface Command extends Terminable {

    String getUsage();

    String getFailureMessage();

    void register(String... aliases);

    default void registerAndBind(TerminableConsumer consumer, String... aliases) {
        this.register(aliases);
        this.bindWith(consumer);
    }

    void call(CommandContext<?> context) throws CommandInterruptException;
}